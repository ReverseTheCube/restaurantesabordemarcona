package com.example.restaurant.generador;

import com.example.restaurant.entidades.DetallePedido;
import com.example.restaurant.entidades.Pedido;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

public class PedidoPdfGenerator {

    /**
     * Genera un archivo PDF para un único pedido.
     *
     * @param pedido La entidad Pedido completa.
     * @param out    El OutputStream donde se escribirá el archivo PDF.
     * @throws IOException Si ocurre un error de E/S.
     */
    public static void generarPdf(Pedido pedido, OutputStream out) throws IOException {
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            // --- Título y Encabezado ---
            document.add(new Paragraph("Resumen de Pedido #" + pedido.getIdPedido())
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            document.add(new Paragraph("Cliente: " + pedido.getCliente().getNombreCliente()));
            document.add(new Paragraph("Atendido por: " + pedido.getUsuario().getNombreUsuario()));
            document.add(new Paragraph("Fecha: " + pedido.getFecha().format(formatter))
                    .setMarginBottom(25));

            // --- Tabla de Detalles ---
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados de la tabla
            addTableHeader(table, "Item");
            addTableHeader(table, "Cant.");
            addTableHeader(table, "P. Unit.");
            addTableHeader(table, "Subtotal");

            // Filas de la tabla
            for (DetallePedido detalle : pedido.getDetalles()) {
                String nombreItem = (detalle.getComida() != null)
                        ? detalle.getComida().getNombreComida()
                        : detalle.getBebida().getNombreBebida();

                table.addCell(nombreItem);
                table.addCell(String.valueOf(detalle.getCantidad())).setTextAlignment(TextAlignment.CENTER);
                table.addCell(String.format("S/. %.2f", detalle.getPrecioUnitario())).setTextAlignment(TextAlignment.RIGHT);
                table.addCell(String.format("S/. %.2f", detalle.getSubtotal())).setTextAlignment(TextAlignment.RIGHT);
            }
            document.add(table);

            // --- Total ---
            document.add(new Paragraph("Total: " + String.format("S/. %.2f", pedido.getTotal()))
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(20));
        }
    }

    private static void addTableHeader(Table table, String headerTitle) {
        table.addHeaderCell(new Cell()
                .add(new Paragraph(headerTitle))
                .setBold()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER));
    }
}