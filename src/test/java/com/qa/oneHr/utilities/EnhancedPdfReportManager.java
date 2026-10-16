package com.qa.oneHr.utilities;


import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
public class EnhancedPdfReportManager {
    private static ThreadLocal<List<StepInfo>> steps =
            ThreadLocal.withInitial(ArrayList::new);

    private static ThreadLocal<ScenarioMeta> meta =
            ThreadLocal.withInitial(ScenarioMeta::new);

    // Every scenario in the run gets appended here; the PDF itself is only
    // written once, at the end of the suite, so the whole run lands in a
    // single OneHrAutomationExtentPdf.pdf alongside the Extent HTML report.
    private static final List<ScenarioRecord> allScenarios =
            Collections.synchronizedList(new ArrayList<>());

    // Lets a step definition swap in the real value it typed/selected (e.g. a
    // randomly generated full name/email) in place of a static feature-file
    // placeholder (like "AUTO"), purely for how that step is displayed in the
    // reports - StepListener consumes this right after the step finishes.
    private static ThreadLocal<String> stepTextOverride = new ThreadLocal<>();

    public static void setNextStepTextOverride(String displayText) {
        stepTextOverride.set(displayText);
    }

    public static String consumeStepTextOverride() {
        String v = stepTextOverride.get();
        stepTextOverride.remove();
        return v;
    }

    // ---------- STORE META ----------
    public static void setScenarioMeta(String browser, String env,
                                       String tags, String user) {
        setScenarioMeta(browser, env, tags, user, "");
    }

    public static void setScenarioMeta(String browser, String env,
                                       String tags, String user, String featureName) {
        ScenarioMeta m = meta.get();
        m.browser = browser;
        m.environment = env;
        m.tags = tags;
        m.user = user;
        m.featureName = featureName;
    }

    // ---------- READ SCENARIOS (for the HTML report) ----------
    public static List<ScenarioRecord> getAllScenarios() {
        return new ArrayList<>(allScenarios);
    }

    // ---------- STORE STEP ----------
    public static void addStep(String step, String status, String base64) {
        steps.get().add(new StepInfo(step, status, base64));
    }

    // Folder shared by the HTML and PDF report for a given run timestamp.
    private static String pdfFilePath(String runTimestamp) {
        return "./OneHrHtml&PDFReports/OneHrReports " + runTimestamp
                + "/test-output/PdfReport/OneHrAutomationExtentPdf.pdf";
    }

    // ---------- RECORD SCENARIO ----------
    // durationText should be like "2 min 15 sec". Records this scenario's steps
    // for the final consolidated PDF and returns the path that PDF will be
    // written to (valid once finalizePdfReport() runs at suite end).
    public static String generatePdf(String scenarioName, String durationText, String runTimestamp) {
        ScenarioRecord record = new ScenarioRecord();
        record.scenarioName = scenarioName;
        record.durationText = durationText;
        record.meta = meta.get();
        record.steps = new ArrayList<>(steps.get());
        allScenarios.add(record);

        // Cleanup ThreadLocal so the next scenario starts fresh
        steps.remove();
        meta.remove();

        return pdfFilePath(runTimestamp);
    }

    // ---------- GENERATE PDF (once, for the whole suite) ----------
    public static void finalizePdfReport(String runTimestamp) {
        String filePath = pdfFilePath(runTimestamp);
        try {
            File dir = new File(filePath).getParentFile();
            if (!dir.exists()) dir.mkdirs();

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            String currentDate = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

            boolean firstScenario = true;
            for (ScenarioRecord record : allScenarios) {
                if (!firstScenario) {
                    doc.add(new AreaBreak());
                }
                firstScenario = false;

                // ---------- HEADER ----------
                doc.add(new Paragraph("AUTOMATION EXECUTION REPORT")
                        .setFont(PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD))
                        .setFontSize(18)
                        .setTextAlignment(TextAlignment.CENTER));

                doc.add(new Paragraph("Scenario : " + record.scenarioName)
                        .setFont(PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD)));

                doc.add(new Paragraph("Date : " + currentDate));
                doc.add(new Paragraph("Duration : " + record.durationText));
                doc.add(new Paragraph("Browser : " + record.meta.browser));
                doc.add(new Paragraph("Environment : " + record.meta.environment));
                doc.add(new Paragraph("Tags : " + record.meta.tags));
                doc.add(new Paragraph("Executed By : " + record.meta.user));
                doc.add(new Paragraph("\n"));

                // ---------- TABLE ----------
                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 5}))
                        .useAllAvailableWidth();
                table.addHeaderCell("Step");
                table.addHeaderCell("Gherkin Step");
                table.addHeaderCell("Screenshot");

                int index = 1;
                for (StepInfo s : record.steps) {
                    table.addCell(String.valueOf(index++));
                    Paragraph p = new Paragraph(s.step);
                    p.setFontColor("FAILED".equalsIgnoreCase(s.status) ? ColorConstants.RED : ColorConstants.GREEN);
                    table.addCell(p);

                    // s.base64 is stored raw (no data-URI prefix), but strip one if ever present -
                    // iText's ImageDataFactory.create(byte[]) auto-detects PNG/JPEG from the bytes.
                    byte[] imgBytes = Base64.getDecoder()
                            .decode(s.base64.replaceFirst("^data:image/\\w+;base64,", ""));
                    Image img = new Image(ImageDataFactory.create(imgBytes));
                    img.setAutoScale(true);
                    img.setMaxHeight(300);
                    table.addCell(new Cell().add(img));
                }

                doc.add(table);
            }

            doc.close();
            allScenarios.clear();

            System.out.println("PDF Generated: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- INNER CLASSES ----------
    public static class StepInfo {
        public String step, status, base64;
        StepInfo(String s, String st, String b) {
            step = s;
            status = st;
            base64 = b;
        }
    }

    public static class ScenarioMeta {
        public String browser = "";
        public String environment = "";
        public String tags = "";
        public String user = "";
        public String featureName = "";
    }

    public static class ScenarioRecord {
        public String scenarioName;
        public String durationText;
        public ScenarioMeta meta;
        public List<StepInfo> steps;
    }
}
