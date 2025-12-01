package com.agente.digitalperu.util;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfGeneratorService {

    private static final DeviceRgb BLUE_COLOR = new DeviceRgb(13, 110, 253);
    private static final DeviceRgb GRAY_COLOR = new DeviceRgb(248, 249, 250);

    public byte[] generateDepositReceipt(
            String customerName,
            String accountNumber,
            BigDecimal amount,
            BigDecimal newBalance,
            String transactionId) throws Exception {
        
        log.info("📄 Generando PDF de depósito para: {}", customerName);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        addHeader(document, "COMPROBANTE DE DEPÓSITO");

        addCustomerInfo(document, customerName, accountNumber);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "Tipo de Operación:", "Depósito");
        addTableRow(table, "Fecha y Hora:", getCurrentDateTime());
        addTableRow(table, "ID de Transacción:", transactionId);
        addTableRow(table, "Monto Depositado:", formatCurrency(amount));
        addTableRow(table, "Nuevo Saldo:", formatCurrency(newBalance), true);

        document.add(table);

        addFooter(document);

        document.close();
        
        log.info("✅ PDF generado exitosamente");
        return baos.toByteArray();
    }

    public byte[] generateWithdrawalReceipt(
            String customerName,
            String accountNumber,
            BigDecimal amount,
            BigDecimal newBalance,
            String transactionId) throws Exception {
        
        log.info("📄 Generando PDF de retiro para: {}", customerName);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        addHeader(document, "COMPROBANTE DE RETIRO");

        addCustomerInfo(document, customerName, accountNumber);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "Tipo de Operación:", "Retiro");
        addTableRow(table, "Fecha y Hora:", getCurrentDateTime());
        addTableRow(table, "ID de Transacción:", transactionId);
        addTableRow(table, "Monto Retirado:", formatCurrency(amount));
        addTableRow(table, "Nuevo Saldo:", formatCurrency(newBalance), true);

        document.add(table);

        addFooter(document);

        document.close();
        
        log.info("✅ PDF generado exitosamente");
        return baos.toByteArray();
    }

    public byte[] generateTransferReceipt(
            String customerName,
            String originAccount,
            String destinationAccount,
            BigDecimal amount,
            BigDecimal newBalance,
            String transactionId) throws Exception {
        
        log.info("📄 Generando PDF de transferencia para: {}", customerName);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        addHeader(document, "COMPROBANTE DE TRANSFERENCIA");

        addCustomerInfo(document, customerName, originAccount);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "Tipo de Operación:", "Transferencia");
        addTableRow(table, "Fecha y Hora:", getCurrentDateTime());
        addTableRow(table, "ID de Transacción:", transactionId);
        addTableRow(table, "Cuenta Origen:", maskAccount(originAccount));
        addTableRow(table, "Cuenta Destino:", maskAccount(destinationAccount));
        addTableRow(table, "Monto Transferido:", formatCurrency(amount));
        addTableRow(table, "Nuevo Saldo:", formatCurrency(newBalance), true);

        document.add(table);

        addFooter(document);

        document.close();
        
        log.info("✅ PDF generado exitosamente");
        return baos.toByteArray();
    }

    private void addHeader(Document document, String title) {
        Paragraph header = new Paragraph("AGENTE DIGITAL PERÚ")
                .setFontSize(20)
                .setBold()
                .setFontColor(BLUE_COLOR)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(header);

        Paragraph subtitle = new Paragraph(title)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);
    }

    private void addCustomerInfo(Document document, String customerName, String accountNumber) {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        infoTable.setMarginBottom(20);

        addTableRow(infoTable, "Cliente:", customerName);
        addTableRow(infoTable, "Cuenta:", maskAccount(accountNumber));

        document.add(infoTable);
    }

    private void addTableRow(Table table, String label, String value) {
        addTableRow(table, label, value, false);
    }

    private void addTableRow(Table table, String label, String value, boolean highlight) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setBold())
                .setBackgroundColor(GRAY_COLOR)
                .setPadding(8);

        Cell valueCell = new Cell()
                .add(new Paragraph(value))
                .setPadding(8);

        if (highlight) {
            valueCell.setBackgroundColor(new DeviceRgb(220, 253, 231))
                     .setBold();
        }

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addFooter(Document document) {
        Paragraph footer = new Paragraph("\n\nEste documento es un comprobante válido de la operación realizada.")
                .setFontSize(10)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30);
        document.add(footer);

        Paragraph disclaimer = new Paragraph("Para cualquier consulta, contacte con su agente bancario.")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(GRAY_COLOR);
        document.add(disclaimer);
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("S/ %,.2f", amount);
    }

    private String maskAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        String last4 = accountNumber.substring(accountNumber.length() - 4);
        return "**** **** " + last4;
    }
}
