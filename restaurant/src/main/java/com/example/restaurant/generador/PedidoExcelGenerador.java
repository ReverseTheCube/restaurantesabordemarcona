package com.example.restaurant.generador;

import com.example.restaurant.entidades.DetallePedido;
import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.entidades.TipoItem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

public class PedidoExcelGenerador {

    /**
     * Genera un archivo Excel para un único pedido.
     * Esta versión está actualizada para trabajar directamente con la entidad Pedido
     * y su lista de detalles, eliminando la dependencia de DTOs obsoletos.
     *
     * @param pedido La entidad Pedido completa, obtenida de la base de datos.
     * @param out    El OutputStream donde se escribirá el archivo Excel.
     * @throws IOException Si ocurre un error de E/S.
     */
    public static void generarExcel(Pedido pedido, OutputStream out) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pedido " + pedido.getIdPedido());

            // --- Estilos para una mejor apariencia ---
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle boldStyle = createBoldStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle totalCurrencyStyle = createTotalCurrencyStyle(workbook, boldStyle);

            int rowIdx = 0;

            // --- Información general del Pedido ---
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            createHeaderInfoRow(sheet, rowIdx++, "ID Pedido:", String.valueOf(pedido.getIdPedido()), boldStyle);
            createHeaderInfoRow(sheet, rowIdx++, "Cliente:", pedido.getCliente().getNombreCliente(), boldStyle);
            createHeaderInfoRow(sheet, rowIdx++, "Fecha:", pedido.getFecha().format(formatter), boldStyle);
            rowIdx++; // Fila en blanco para espaciar

            // --- Encabezado de la tabla de detalles ---
            Row tableHeaderRow = sheet.createRow(rowIdx++);
            String[] headers = {"Categoría", "Item", "Cantidad", "Precio Unitario", "Subtotal"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = tableHeaderRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- Filas con los datos de los detalles del pedido ---
            for (DetallePedido detalle : pedido.getDetalles()) {
                Row row = sheet.createRow(rowIdx++);
                String categoria = "Desconocido";
                String nombreItem = "N/A";

                // Determina el nombre y categoría basado en el tipo de item
                if (detalle.getTipoItem() == TipoItem.COMIDA && detalle.getComida() != null) {
                    categoria = capitalize(detalle.getComida().getTipoComida().toString());
                    nombreItem = detalle.getComida().getNombreComida();
                } else if (detalle.getTipoItem() == TipoItem.BEBIDA && detalle.getBebida() != null) {
                    categoria = "Bebida";
                    nombreItem = detalle.getBebida().getNombreBebida();
                }

                row.createCell(0).setCellValue(categoria);
                row.createCell(1).setCellValue(nombreItem);
                row.createCell(2).setCellValue(detalle.getCantidad());

                // Para precios y subtotales, convierte BigDecimal a double y aplica formato de moneda
                Cell precioCell = row.createCell(3);
                precioCell.setCellValue(detalle.getPrecioUnitario().doubleValue());
                precioCell.setCellStyle(currencyStyle);

                Cell subtotalCell = row.createCell(4);
                subtotalCell.setCellValue(detalle.getSubtotal().doubleValue());
                subtotalCell.setCellStyle(currencyStyle);
            }

            // --- Fila del Total General ---
            rowIdx++; // Fila en blanco para espaciar
            Row totalRow = sheet.createRow(rowIdx);
            Cell totalLabelCell = totalRow.createCell(headers.length - 2);
            totalLabelCell.setCellValue("Total General:");
            totalLabelCell.setCellStyle(boldStyle);

            Cell totalValueCell = totalRow.createCell(headers.length - 1);
            totalValueCell.setCellValue(pedido.getTotal().doubleValue());
            totalValueCell.setCellStyle(totalCurrencyStyle);

            // --- Auto-ajustar el tamaño de las columnas ---
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        }
    }

    // --- Métodos de ayuda para crear estilos y mantener el código principal limpio ---

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("S/ #,##0.00"));
        return style;
    }

    private static CellStyle createTotalCurrencyStyle(Workbook workbook, CellStyle boldStyle) {
        CellStyle style = createCurrencyStyle(workbook);
        // Corrección: Obtener la fuente desde el workbook usando el índice del estilo 'boldStyle'
        Font font = workbook.getFontAt(boldStyle.getFontIndex());
        style.setFont(font);
        return style;
    }

    private static void createHeaderInfoRow(Sheet sheet, int rowIdx, String label, String value, CellStyle labelStyle) {
        Row row = sheet.createRow(rowIdx);
        Cell cellLabel = row.createCell(0);
        cellLabel.setCellValue(label);
        cellLabel.setCellStyle(labelStyle);
        row.createCell(1).setCellValue(value);
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}