const fs = require("fs");
const path = require("path");
const crypto = require("crypto");

const CUCUMBER_JSON_PATH = path.join(__dirname, "..", "target", "cucumber.json");
const OUTPUT_DIR = path.join(__dirname, "..", "target", "mochawesome-report");
const OUTPUT_JSON_PATH = path.join(OUTPUT_DIR, "mochawesome.json");
const SCREENSHOTS_DIR = path.join(OUTPUT_DIR, "screenshots");

function sanitize(name) {
  return (name || "").replace(/[^a-zA-Z0-9]/g, "_");
}

function findScreenshotContext(scenarioName, stepIndex) {
  // Step screenshots are saved as JPEG (see StepListener.saveScreenshotForMochawesome),
  // downscaled and compressed to keep report sizes down.
  const screenshotPath = path.join(SCREENSHOTS_DIR, sanitize(scenarioName), `step_${stepIndex}.jpg`);
  if (!fs.existsSync(screenshotPath)) return null;

  const base64Image = fs.readFileSync(screenshotPath).toString("base64");
  return JSON.stringify([
    { title: "Screenshot", value: `data:image/jpeg;base64,${base64Image}` },
  ]);
}

function nsToMs(durationNs) {
  return durationNs ? Math.round(durationNs / 1e6) : 0;
}

function stepState(status) {
  if (status === "passed") return "passed";
  if (status === "failed") return "failed";
  return "pending"; // skipped, undefined, pending, ambiguous
}

function buildStepTest(step, scenarioUuid, scenarioName, stepIndex) {
  const status = step.result ? step.result.status : "skipped";
  const state = stepState(status);
  const title = `${step.keyword || ""}${step.name || ""}`.trim();

  return {
    title,
    fullTitle: `${scenarioName} ${title}`.trim(),
    timedOut: false,
    duration: nsToMs(step.result && step.result.duration),
    state,
    speed: "fast",
    pass: state === "passed",
    fail: state === "failed",
    pending: state === "pending",
    context: findScreenshotContext(scenarioName, stepIndex),
    code: "",
    err: state === "failed"
      ? {
          message: (step.result && step.result.error_message) || "Step failed",
          estack: (step.result && step.result.error_message) || "",
        }
      : {},
    uuid: crypto.randomUUID(),
    parentUUID: scenarioUuid,
    isHook: false,
    skipped: state === "pending",
  };
}

function buildScenarioSuite(scenario, featureUuid) {
  const scenarioUuid = crypto.randomUUID();
  const steps = scenario.steps || [];
  const tests = steps.map((step, idx) => buildStepTest(step, scenarioUuid, scenario.name, idx + 1));

  const passes = tests.filter((t) => t.pass).map((t) => t.uuid);
  const failures = tests.filter((t) => t.fail).map((t) => t.uuid);
  const pending = tests.filter((t) => t.pending).map((t) => t.uuid);
  const duration = tests.reduce((sum, t) => sum + t.duration, 0);

  return {
    uuid: scenarioUuid,
    title: scenario.name,
    fullFile: "",
    file: "",
    beforeHooks: [],
    afterHooks: [],
    tests,
    suites: [],
    passes,
    failures,
    pending,
    skipped: [],
    duration,
    root: false,
    rootEmpty: tests.length === 0,
    _timeout: 2000,
  };
}

function buildFeatureSuite(feature) {
  const featureUuid = crypto.randomUUID();
  const scenarios = (feature.elements || []).filter((el) => el.type === "scenario");
  const suites = scenarios.map((scenario) => buildScenarioSuite(scenario, featureUuid));
  const duration = suites.reduce((sum, s) => sum + s.duration, 0);

  return {
    uuid: featureUuid,
    title: feature.name,
    fullFile: feature.uri || "",
    file: feature.uri || "",
    beforeHooks: [],
    afterHooks: [],
    tests: [],
    suites,
    passes: [],
    failures: [],
    pending: [],
    skipped: [],
    duration,
    root: false,
    rootEmpty: true,
    _timeout: 2000,
  };
}

function collectAllTests(suite, acc) {
  acc.push(...suite.tests);
  suite.suites.forEach((child) => collectAllTests(child, acc));
  return acc;
}

function main() {
  if (!fs.existsSync(CUCUMBER_JSON_PATH)) {
    console.error(
      `Could not find ${CUCUMBER_JSON_PATH}. Run "mvn test" first so Cucumber writes target/cucumber.json.`
    );
    process.exit(1);
  }

  const features = JSON.parse(fs.readFileSync(CUCUMBER_JSON_PATH, "utf8"));
  const rootUuid = crypto.randomUUID();
  const featureSuites = features.map(buildFeatureSuite);

  const allTests = featureSuites.reduce((acc, suite) => collectAllTests(suite, acc), []);
  const passes = allTests.filter((t) => t.pass).length;
  const failures = allTests.filter((t) => t.fail).length;
  const pending = allTests.filter((t) => t.pending).length;
  const totalDuration = featureSuites.reduce((sum, s) => sum + s.duration, 0);

  const now = new Date();
  const start = new Date(now.getTime() - totalDuration);

  const report = {
    stats: {
      suites: featureSuites.length + featureSuites.reduce((n, s) => n + s.suites.length, 0),
      tests: allTests.length,
      passes,
      pending,
      failures,
      start: start.toISOString(),
      end: now.toISOString(),
      duration: totalDuration,
      testsRegistered: allTests.length,
      passPercent: allTests.length ? Number(((passes / allTests.length) * 100).toFixed(2)) : 0,
      pendingPercent: allTests.length ? Number(((pending / allTests.length) * 100).toFixed(2)) : 0,
      other: 0,
      hasOther: false,
      skipped: 0,
      hasSkipped: false,
    },
    results: [
      {
        uuid: rootUuid,
        title: "",
        fullFile: "",
        file: "",
        beforeHooks: [],
        afterHooks: [],
        tests: [],
        suites: featureSuites,
        passes: [],
        failures: [],
        pending: [],
        skipped: [],
        duration: totalDuration,
        root: true,
        rootEmpty: true,
        _timeout: 2000,
      },
    ],
    meta: {
      mocha: { version: "n/a" },
      mochawesome: { options: {}, version: "n/a" },
      marge: { options: {}, version: "n/a" },
    },
  };

  fs.mkdirSync(OUTPUT_DIR, { recursive: true });
  fs.writeFileSync(OUTPUT_JSON_PATH, JSON.stringify(report, null, 2));
  console.log(`Wrote Mochawesome-format JSON to ${OUTPUT_JSON_PATH}`);
}

main();
