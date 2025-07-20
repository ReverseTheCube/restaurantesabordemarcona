package com.example.restaurant.generador;

import com.example.restaurant.entidades.DetallePedido;
import com.example.restaurant.entidades.Pedido;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream; // <-- Importante: Se usa el genérico
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistorialExcelGenerador {

    // ==================== INICIO DE LA CORRECCIÓN ====================
    // Se cambia ByteArrayOutputStream por el más genérico OutputStream.
    // Ahora puede escribir en un archivo, en la memoria o en una respuesta web.
    public static void generarExcel(List<Pedido> pedidos, OutputStream out) throws IOException {
        // ===================== FIN DE LA CORRECCIÓN ======================
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historial de Ventas");

            // Estilo para los encabezados
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Crear la fila de encabezado
            String[] columns = {"ID Pedido", "Fecha", "Cliente", "Atendido por", "Item", "Cantidad", "P. Unitario", "Subtotal"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowNum = 1;
            // Formateador para fecha y hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Llenar los datos
            for (Pedido pedido : pedidos) {
                for (DetallePedido detalle : pedido.getDetalles()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(pedido.getIdPedido());
                    row.createCell(1).setCellValue(pedido.getFecha().format(formatter));
                    row.createCell(2).setCellValue(pedido.getCliente().getNombreCliente());
                    row.createCell(3).setCellValue(pedido.getUsuario().getNombreUsuario());

                    String nombreItem;
                    if (detalle.getComida() != null) {
                        nombreItem = detalle.getComida().getNombreComida();
                    } else if (detalle.getBebida() != null) {
                        nombreItem = detalle.getBebida().getNombreBebida();
                    } else {
                        nombreItem = "Item no especificado"; // Fallback por si acaso
                    }
                    row.createCell(4).setCellValue(nombreItem);

                    row.createCell(5).setCellValue(detalle.getCantidad());
                    row.createCell(6).setCellValue(detalle.getPrecioUnitario().doubleValue());
                    row.createCell(7).setCellValue(detalle.getSubtotal().doubleValue());
                }
            }

            // Autoajustar el tamaño de las columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        }
    }
}