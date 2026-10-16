package com.qa.oneHr.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

/**
 * Builds a single, self-contained "OneHr_ExecutionReport.html" for the whole
 * suite run, using the scenario/step/screenshot data already collected by
 * EnhancedPdfReportManager (the same data source the consolidated PDF uses).
 * Screenshots and the NforceOne logo are embedded as base64 so the report
 * still works if the folder is zipped and shared as-is.
 */
public class OneHrHtmlReportGenerator {

    public static void generate(String runTimestamp, String executionId,
                                 LocalDateTime suiteStart, LocalDateTime suiteEnd,
                                 String chromeVersion) {
        try {
            List<EnhancedPdfReportManager.ScenarioRecord> scenarios = EnhancedPdfReportManager.getAllScenarios();

            String filePath = "./OneHrHtml&PDFReports/OneHrReports " + runTimestamp + "/OneHr_ExecutionReport.html";
            File file = new File(filePath);
            if (file.getParentFile() != null) file.getParentFile().mkdirs();

            String html = buildHtml(scenarios, executionId, suiteStart, suiteEnd, chromeVersion);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(html.getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("OneHr Execution Report Generated: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildHtml(List<EnhancedPdfReportManager.ScenarioRecord> scenarios,
                                     String executionId, LocalDateTime suiteStart, LocalDateTime suiteEnd,
                                     String chromeVersion) {

        int totalScenarios = scenarios.size();
        int totalSteps = 0, passedSteps = 0, failedSteps = 0;
        int passedScenarios = 0, failedScenarios = 0;

        for (EnhancedPdfReportManager.ScenarioRecord r : scenarios) {
            boolean scenarioFailed = false;
            for (EnhancedPdfReportManager.StepInfo s : r.steps) {
                totalSteps++;
                if ("FAILED".equalsIgnoreCase(s.status)) {
                    failedSteps++;
                    scenarioFailed = true;
                } else {
                    passedSteps++;
                }
            }
            if (scenarioFailed) failedScenarios++; else passedScenarios++;
        }
        int skippedScenarios = 0;
        int skippedSteps = 0;
        int passRate = totalScenarios == 0 ? 0 : Math.round(passedScenarios * 100f / totalScenarios);
        String overallStatus = failedScenarios > 0 ? "FAILED" : "PASSED";

        DateTimeFormatter tf = DateTimeFormatter.ofPattern("M/d/yyyy hh:mm:ss a");
        String startTimeText = suiteStart != null ? suiteStart.format(tf) : "Unknown";
        String endTimeText = suiteEnd != null ? suiteEnd.format(tf) : "Unknown";
        String generatedText = LocalDateTime.now().format(tf);
        String durationText = (suiteStart != null && suiteEnd != null)
                ? formatDuration(Duration.between(suiteStart, suiteEnd))
                : "Unknown";

        String browser = ConfigurationUtilities.getProperty("browser");
        String environment = ConfigurationUtilities.getProperty("Environment");
        String logoBase64 = loadLogoBase64();

        String os = System.getProperty("os.name");
        String javaVersion = System.getProperty("java.version");
        String seleniumVersion = versionOf(org.openqa.selenium.remote.SessionId.class, null);
        if (seleniumVersion == null) seleniumVersion = readPomProperty("selenium.version");
        if (seleniumVersion == null) seleniumVersion = "Unknown";
        String cucumberVersion = versionOf(io.cucumber.java.Scenario.class, "Unknown");
        String chromeVer = (chromeVersion == null || chromeVersion.isEmpty()) ? "Unknown" : chromeVersion;

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        sb.append("<title>OneHr Automation - Execution Report</title>");
        sb.append(buildCss());
        sb.append("</head><body>");

        // ---------- SIDEBAR ----------
        sb.append("<div class='sidebar'>");
        sb.append("<div class='sidebar-header'>");
        sb.append("<img class='logo-sm' src='data:image/png;base64,").append(logoBase64).append("' alt='NforceOne'/>");
        sb.append("<div><div class='brand-title'>NForce OneHr</div><div class='brand-sub'>Execution Report</div></div>");
        sb.append("</div>");

        sb.append("<div class='nav-group-label'>OVERVIEW</div>");
        sb.append("<a class='nav-link' href='#execution-overview'>Execution Overview</a>");
        sb.append("<a class='nav-link' href='#execution-summary'>Execution Summary</a>");
        sb.append("<a class='nav-link' href='#test-results'>Test Results</a>");
        sb.append("<a class='nav-link' href='#test-steps'>Test Steps</a>");
        sb.append("<a class='nav-link' href='#system-information'>System Information</a>");

        sb.append("<div class='nav-group-label'>TEST SUITE</div>");
        for (int i = 0; i < scenarios.size(); i++) {
            EnhancedPdfReportManager.ScenarioRecord r = scenarios.get(i);
            String status = scenarioStatus(r);
            sb.append("<a class='nav-scenario' href='#scenario-").append(i).append("'>");
            sb.append("<span class='nav-scenario-name'>").append(escape(r.scenarioName)).append("</span>");
            sb.append("<span class='pill pill-").append(status.toLowerCase()).append("'>").append(status).append("</span>");
            sb.append("</a>");
        }
        sb.append("</div>"); // sidebar

        // ---------- MAIN ----------
        sb.append("<div class='main'>");

        // top header row
        sb.append("<div class='topbar'>");
        sb.append("<div class='topbar-left'>");
        sb.append("<img class='logo-md' src='data:image/png;base64,").append(logoBase64).append("' alt='NforceOne'/>");
        sb.append("<h1>Execution Report</h1>");
        sb.append("</div>");
        sb.append("<div class='topbar-actions'>");
        sb.append("<button class='btn btn-light' onclick='window.print()'>Print</button>");
        sb.append("<button class='btn btn-dark' onclick='downloadReport()'>Download</button>");
        sb.append("</div></div>");

        // main info card
        sb.append("<div class='card main-info-card'>");
        sb.append("<div class='main-info-top'>");
        sb.append("<div><div class='report-title'>NForce OneHr</div>");
        sb.append("<div class='report-subtitle'>Sanity Suite - ").append(escape(browser)).append("</div></div>");
        sb.append("<span class='pill pill-").append(overallStatus.toLowerCase()).append(" pill-lg'>").append(overallStatus).append("</span>");
        sb.append("</div>");
        sb.append("<div class='info-grid'>");
        sb.append(infoItem("PROJECT", "NForce OneHr"));
        sb.append(infoItem("SUITE / SCRIPT", "Sanity Suite - " + browser));
        sb.append(infoItem("EXECUTION ID", executionId));
        sb.append(infoItem("BROWSER", browser));
        sb.append(infoItem("ENVIRONMENT", environment));
        sb.append(infoItem("START TIME", startTimeText));
        sb.append(infoItem("END TIME", endTimeText));
        sb.append(infoItem("DURATION", durationText));
        sb.append(infoItem("GENERATED", generatedText));
        sb.append("</div></div>");

        // ---------- EXECUTION OVERVIEW ----------
        sb.append("<h2 class='section-title' id='execution-overview'>Execution Overview</h2>");
        sb.append("<div class='overview-cards'>");
        sb.append(overviewCard("Features / Scripts", String.valueOf(totalScenarios)));
        sb.append(overviewCard("Steps", String.valueOf(totalSteps)));
        sb.append(overviewCard("Pass Rate", passRate + "%"));
        sb.append("</div>");
        sb.append("<div class='mini-counts'>");
        sb.append(miniCount("Passed", passedScenarios, "passed"));
        sb.append(miniCount("Failed", failedScenarios, "failed"));
        sb.append(miniCount("Skipped", skippedScenarios, "skipped"));
        sb.append("</div>");

        // ---------- EXECUTION SUMMARY ----------
        sb.append("<h2 class='section-title' id='execution-summary'>Execution Summary</h2>");
        sb.append("<div class='card'>");
        sb.append("<table class='summary-table'><thead><tr><th>Metric</th><th>Features / Scripts</th><th>Steps</th></tr></thead><tbody>");
        sb.append(summaryRow("Passed", passedScenarios, passedSteps, "passed"));
        sb.append(summaryRow("Failed", failedScenarios, failedSteps, "failed"));
        sb.append(summaryRow("Skipped", skippedScenarios, skippedSteps, "skipped"));
        sb.append("<tr class='total-row'><td>Total</td><td>").append(totalScenarios).append("</td><td>").append(totalSteps).append("</td></tr>");
        sb.append("</tbody></table>");
        sb.append("<div class='total-duration'>Total Duration: <strong>").append(durationText).append("</strong></div>");
        sb.append("</div>");

        // ---------- TEST RESULTS ----------
        sb.append("<h2 class='section-title' id='test-results'>Test Results</h2>");
        sb.append("<div class='card'>");
        sb.append("<table class='results-table'><thead><tr><th>Scenario</th><th>Feature</th><th>Status</th><th>Duration</th><th>Steps</th><th>Passed</th><th>Failed</th><th>Skipped</th></tr></thead><tbody>");
        for (int i = 0; i < scenarios.size(); i++) {
            EnhancedPdfReportManager.ScenarioRecord r = scenarios.get(i);
            int pSteps = 0, fSteps = 0;
            for (EnhancedPdfReportManager.StepInfo s : r.steps) {
                if ("FAILED".equalsIgnoreCase(s.status)) fSteps++; else pSteps++;
            }
            String status = scenarioStatus(r);
            sb.append("<tr class='result-row' onclick=\"location.hash='#scenario-").append(i).append("'\">");
            sb.append("<td>").append(escape(r.scenarioName)).append("</td>");
            sb.append("<td>").append(escape(r.meta.featureName)).append("</td>");
            sb.append("<td><span class='pill pill-").append(status.toLowerCase()).append("'>").append(status).append("</span></td>");
            sb.append("<td>").append(escape(r.durationText)).append("</td>");
            sb.append("<td>").append(r.steps.size()).append("</td>");
            sb.append("<td>").append(pSteps).append("</td>");
            sb.append("<td>").append(fSteps).append("</td>");
            sb.append("<td>0</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table></div>");

        // ---------- TEST STEPS ----------
        sb.append("<h2 class='section-title' id='test-steps'>Test Steps</h2>");
        for (int i = 0; i < scenarios.size(); i++) {
            EnhancedPdfReportManager.ScenarioRecord r = scenarios.get(i);
            String status = scenarioStatus(r);
            sb.append("<div class='card scenario-card' id='scenario-").append(i).append("'>");
            sb.append("<div class='scenario-card-header'>");
            sb.append("<div><div class='scenario-name'>").append(escape(r.scenarioName)).append("</div>");
            sb.append("<div class='scenario-meta'>").append(escape(r.meta.featureName))
                    .append(" &middot; ").append(escape(r.durationText)).append("</div></div>");
            sb.append("<span class='pill pill-").append(status.toLowerCase()).append("'>").append(status).append("</span>");
            sb.append("</div>");

            int stepNo = 1;
            for (EnhancedPdfReportManager.StepInfo s : r.steps) {
                String stStatus = "FAILED".equalsIgnoreCase(s.status) ? "FAILED" : "PASSED";
                sb.append("<div class='step-row'>");
                sb.append("<div class='step-left'>");
                sb.append("<div class='step-title'>Step ").append(stepNo++).append("</div>");
                sb.append("<div class='step-text'>").append(escape(s.step)).append("</div>");
                sb.append("<span class='pill pill-").append(stStatus.toLowerCase()).append("'>").append(stStatus).append("</span>");
                sb.append("</div>");
                if (s.base64 != null && !s.base64.isEmpty()) {
                    sb.append("<div class='step-right'>");
                    // Step screenshots are captured as JPEG (see ReusableMethods.CaptureScreenShotBase64)
                    // to keep this report - which embeds every step's screenshot inline - well under 5MB.
                    sb.append("<img class='thumb' src='data:image/jpeg;base64,").append(s.base64)
                            .append("' onclick=\"openLightbox(this.src)\"/>");
                    sb.append("</div>");
                }
                sb.append("</div>");
            }
            sb.append("</div>");
        }

        // ---------- SYSTEM INFORMATION ----------
        sb.append("<h2 class='section-title' id='system-information'>System Information</h2>");
        sb.append("<div class='card'>");
        sb.append("<div class='info-grid'>");
        sb.append(infoItem("OPERATING SYSTEM", os));
        sb.append(infoItem("JAVA VERSION", javaVersion));
        sb.append(infoItem("SELENIUM VERSION", seleniumVersion));
        sb.append(infoItem("CHROME VERSION", chromeVer));
        sb.append(infoItem("CUCUMBER VERSION", cucumberVersion));
        sb.append(infoItem("EXECUTION ENVIRONMENT", environment));
        sb.append("</div></div>");

        sb.append("</div>"); // main

        // ---------- LIGHTBOX ----------
        sb.append("<div id='lightbox' class='lightbox' onclick='closeLightbox()'>");
        sb.append("<span class='lightbox-close'>&times;</span>");
        sb.append("<img id='lightbox-img' src=''/>");
        sb.append("</div>");

        sb.append(buildJs());
        sb.append("</body></html>");

        return sb.toString();
    }

    private static String scenarioStatus(EnhancedPdfReportManager.ScenarioRecord r) {
        for (EnhancedPdfReportManager.StepInfo s : r.steps) {
            if ("FAILED".equalsIgnoreCase(s.status)) return "FAILED";
        }
        return "PASSED";
    }

    private static String infoItem(String label, String value) {
        return "<div class='info-item'><div class='info-label'>" + escape(label) +
                "</div><div class='info-value'>" + escape(value) + "</div></div>";
    }

    private static String overviewCard(String label, String value) {
        return "<div class='card overview-card'><div class='overview-value'>" + escape(value) +
                "</div><div class='overview-label'>" + escape(label) + "</div></div>";
    }

    private static String miniCount(String label, int count, String cssClass) {
        return "<div class='mini-count mini-" + cssClass + "'><span class='dot dot-" + cssClass + "'></span>" +
                escape(label) + ": <strong>" + count + "</strong></div>";
    }

    private static String summaryRow(String label, int scenarioCount, int stepCount, String cssClass) {
        return "<tr><td><span class='dot dot-" + cssClass + "'></span>" + escape(label) + "</td><td>" +
                scenarioCount + "</td><td>" + stepCount + "</td></tr>";
    }

    private static String formatDuration(Duration d) {
        long h = d.toHours();
        long m = d.toMinutesPart();
        long s = d.toSecondsPart();
        return String.format("%02d:%02d:%02d", h, m, s);
    }


    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String versionOf(Class<?> clazz, String fallback) {
        String v = clazz.getPackage().getImplementationVersion();
        return v == null || v.isEmpty() ? fallback : v;
    }

    // Reads a <tagName>value</tagName> property straight out of this project's pom.xml,
    // for runtime values (like the Selenium version) whose jar manifest doesn't expose
    // an Implementation-Version. Reflects the actual build config, not an invented value.
    private static String readPomProperty(String tagName) {
        try {
            String pom = new String(java.nio.file.Files.readAllBytes(new File("pom.xml").toPath()), StandardCharsets.UTF_8);
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("<" + tagName + ">([^<]+)</" + tagName + ">").matcher(pom);
            return m.find() ? m.group(1).trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String loadLogoBase64() {
        try (InputStream is = OneHrHtmlReportGenerator.class.getClassLoader()
                .getResourceAsStream("assets/nforceone_logo.png")) {
            if (is == null) return "";
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }

    private static String buildCss() {
        return "<style>" +
                "*{box-sizing:border-box;margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif;}" +
                "body{background:#f4f6f9;color:#1f2937;display:flex;}" +
                ".sidebar{width:280px;background:#ffffff;border-right:1px solid #e5e7eb;height:100vh;position:fixed;overflow-y:auto;padding:20px 0;}" +
                ".sidebar-header{display:flex;align-items:center;gap:12px;padding:0 20px 20px 20px;border-bottom:1px solid #eee;margin-bottom:16px;}" +
                ".logo-sm{width:44px;height:44px;border-radius:50%;object-fit:cover;border:2px solid #111;}" +
                ".logo-md{width:56px;height:56px;border-radius:50%;object-fit:cover;border:2px solid #111;}" +
                ".brand-title{font-weight:700;font-size:15px;}" +
                ".brand-sub{font-size:12px;color:#6b7280;}" +
                ".nav-group-label{font-size:11px;font-weight:700;color:#9ca3af;letter-spacing:.05em;padding:14px 20px 6px 20px;}" +
                ".nav-link{display:block;padding:9px 20px;color:#374151;text-decoration:none;font-size:14px;}" +
                ".nav-link:hover{background:#f3f4f6;}" +
                ".nav-scenario{display:flex;flex-direction:column;align-items:flex-start;gap:6px;padding:9px 20px;text-decoration:none;color:#374151;font-size:13px;}" +
                ".nav-scenario:hover{background:#f3f4f6;}" +
                ".nav-scenario-name{white-space:normal;word-break:break-word;line-height:1.4;}" +
                ".main{margin-left:280px;padding:28px 32px;width:calc(100% - 280px);}" +
                ".topbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;}" +
                ".topbar-left{display:flex;align-items:center;gap:14px;}" +
                ".topbar-left h1{font-size:22px;font-weight:700;}" +
                ".topbar-actions{display:flex;gap:10px;}" +
                ".btn{padding:10px 20px;border-radius:8px;border:1px solid #d1d5db;font-weight:600;font-size:14px;cursor:pointer;}" +
                ".btn-light{background:#fff;color:#111;}" +
                ".btn-dark{background:#111827;color:#fff;border-color:#111827;}" +
                ".card{background:#fff;border:1px solid #e5e7eb;border-radius:14px;padding:24px;margin-bottom:26px;box-shadow:0 1px 2px rgba(0,0,0,.03);}" +
                ".main-info-top{display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:18px;}" +
                ".report-title{font-size:26px;font-weight:800;}" +
                ".report-subtitle{color:#6b7280;margin-top:4px;}" +
                ".info-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:22px 24px;border-top:1px solid #eee;padding-top:18px;}" +
                ".info-label{font-size:11px;color:#9ca3af;letter-spacing:.04em;margin-bottom:4px;}" +
                ".info-value{font-weight:600;font-size:14px;}" +
                ".pill{display:inline-block;padding:5px 14px;border-radius:999px;font-size:12px;font-weight:700;}" +
                ".pill-lg{font-size:14px;padding:8px 18px;}" +
                ".pill-passed{background:#dcfce7;color:#16a34a;}" +
                ".pill-failed{background:#fee2e2;color:#dc2626;}" +
                ".pill-skipped{background:#fef3c7;color:#d97706;}" +
                ".section-title{font-size:19px;font-weight:700;margin:6px 0 16px 0;padding-left:12px;border-left:4px solid #ef4444;}" +
                ".overview-cards{display:grid;grid-template-columns:repeat(3,1fr);gap:20px;margin-bottom:16px;}" +
                ".overview-card{text-align:center;}" +
                ".overview-value{font-size:34px;font-weight:800;}" +
                ".overview-label{color:#6b7280;margin-top:6px;}" +
                ".mini-counts{display:flex;gap:24px;margin-bottom:28px;flex-wrap:wrap;}" +
                ".mini-count{font-size:14px;color:#374151;}" +
                ".dot{display:inline-block;width:9px;height:9px;border-radius:50%;margin-right:6px;}" +
                ".dot-passed{background:#16a34a;}.dot-failed{background:#dc2626;}.dot-skipped{background:#d97706;}" +
                ".summary-table,.results-table{width:100%;border-collapse:collapse;}" +
                ".summary-table th,.summary-table td,.results-table th,.results-table td{padding:12px 10px;border-bottom:1px solid #f0f0f0;text-align:left;font-size:14px;}" +
                ".summary-table th,.results-table th{color:#6b7280;font-size:12px;text-transform:uppercase;letter-spacing:.03em;}" +
                ".total-row{font-weight:700;}" +
                ".total-duration{margin-top:14px;color:#374151;}" +
                ".results-table tr.result-row{cursor:pointer;}" +
                ".results-table tr.result-row:hover{background:#f9fafb;}" +
                ".scenario-card-header{display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:14px;padding-bottom:14px;border-bottom:1px solid #eee;}" +
                ".scenario-name{font-weight:700;font-size:16px;}" +
                ".scenario-meta{color:#9ca3af;font-size:12px;margin-top:3px;}" +
                ".step-row{display:flex;justify-content:space-between;align-items:center;padding:14px 0;border-bottom:1px solid #f3f4f6;gap:16px;}" +
                ".step-row:last-child{border-bottom:none;}" +
                ".step-title{font-weight:700;font-size:13px;color:#6b7280;}" +
                ".step-text{font-size:14px;margin:4px 0 8px 0;}" +
                ".thumb{width:96px;height:64px;object-fit:cover;border-radius:8px;border:1px solid #e5e7eb;cursor:pointer;}" +
                ".lightbox{display:none;position:fixed;z-index:999;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,.85);text-align:center;}" +
                ".lightbox img{max-width:90%;max-height:90%;margin-top:3vh;border-radius:6px;}" +
                ".lightbox-close{position:absolute;top:18px;right:32px;font-size:36px;color:#fff;cursor:pointer;}" +
                "@media print{.sidebar,.topbar-actions{display:none!important;}.main{margin-left:0;width:100%;}}" +
                "</style>";
    }

    private static String buildJs() {
        return "<script>" +
                "function openLightbox(src){document.getElementById('lightbox-img').src=src;document.getElementById('lightbox').style.display='block';}" +
                "function closeLightbox(){document.getElementById('lightbox').style.display='none';}" +
                "function downloadReport(){" +
                "var blob=new Blob([document.documentElement.outerHTML],{type:'text/html'});" +
                "var a=document.createElement('a');a.href=URL.createObjectURL(blob);a.download='OneHr_ExecutionReport.html';" +
                "document.body.appendChild(a);a.click();document.body.removeChild(a);}" +
                "</script>";
    }
}
