package com.example.restaurant.Controlador;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.example.restaurant.dto.TaskRequest;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final List<Map<String, Object>> tasks = new ArrayList<>();
    // Constructor para agregar datos de prueba
    public TaskController() {
        // Datos de prueba
        Map<String, Object> task1 = new HashMap<>();
        Map<String, String> worker1 = new HashMap<>();
        worker1.put("name", "Carlos García");
        worker1.put("role", "Chef Principal");

        task1.put("worker", worker1);
        task1.put("title", "Preparar menú especial");
        task1.put("description", "Menú para evento corporativo");
        task1.put("priority", "high");
        task1.put("dueTime", "2025-06-13T14:00");
        task1.put("type", "preparar_comida");
        tasks.add(task1);

        Map<String, Object> task2 = new HashMap<>();
        task2.put("worker", worker1);
        task2.put("title", "Revisar inventario");
        task2.put("description", "Control de stock semanal");
        task2.put("priority", "medium");
        task2.put("dueTime", "2025-06-14T09:00");
        task2.put("type", "inventario");
        tasks.add(task2);
    }
    @PostMapping("/tasks")
    public Map<String, String> assignTask(@RequestBody TaskRequest taskData) {
        if (StringUtils.isBlank(taskData.getTitle())) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("worker", taskData.getWorker());
        taskMap.put("title", taskData.getTitle());
        taskMap.put("description", taskData.getDescription());
        taskMap.put("dueTime", taskData.getDueTime());
        taskMap.put("priority", taskData.getPriority());
        taskMap.put("type", taskData.getType());

        tasks.add(taskMap);

        String assignedTo = String.format(
                "Tarea asignada a %s (%s)",
                taskData.getWorker().getName(),
                taskData.getWorker().getRole()
        );
        logger.info(assignedTo);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", assignedTo);
        response.put("timestamp", LocalDateTime.now().toString());

        return response;
    }

    @GetMapping("/report")
    public void generateReport(@RequestParam(required = false) String worker, HttpServletResponse response) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            logger.info("=== INICIANDO GENERACIÓN DE REPORTE ===");
            logger.info("Trabajador solicitado: '{}'", worker != null ? worker : "TODOS");
            logger.info("Total de tareas registradas en memoria: {}", tasks.size());

            Sheet sheet = workbook.createSheet("Tareas");

            // Crear cabecera del Excel
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Trabajador");
            headerRow.createCell(1).setCellValue("Rol");
            headerRow.createCell(2).setCellValue("Título");
            headerRow.createCell(3).setCellValue("Descripción");
            headerRow.createCell(4).setCellValue("Tipo");
            headerRow.createCell(5).setCellValue("Prioridad");
            headerRow.createCell(6).setCellValue("Fecha Límite");

            // Si no se especifica trabajador, mostrar todas las tareas
            List<Map<String, Object>> filteredTasks;
            if (worker == null || worker.trim().isEmpty()) {
                logger.info("Generando reporte de TODAS las tareas");
                filteredTasks = new ArrayList<>(tasks);
            } else {
                logger.info("Filtrando tareas para trabajador: '{}'", worker);
                filteredTasks = tasks.stream()
                        .filter(task -> {
                            try {
                                Object workerObj = task.get("worker");
                                if (workerObj instanceof Map) {
                                    String taskWorkerName = ((Map<?, ?>) workerObj).get("name").toString();
                                    return worker.trim().equalsIgnoreCase(taskWorkerName.trim());
                                }
                                return false;
                            } catch (Exception e) {
                                logger.error("Error procesando tarea: {}", task, e);
                                return false;
                            }
                        })
                        .toList();
            }

            logger.info("Tareas a incluir en reporte: {}", filteredTasks.size());

            // Verificar si hay tareas
            if (filteredTasks.isEmpty()) {
                String errorMsg = worker != null ?
                        String.format("No se encontraron tareas para '%s'", worker) :
                        "No hay tareas registradas en el sistema";
                logger.warn(errorMsg);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write(errorMsg);
                return;
            }

            // Llenar datos en el Excel
            int rowNum = 1;
            for (Map<String, Object> task : filteredTasks) {
                Row row = sheet.createRow(rowNum);

                Object workerObj = task.get("worker");
                String workerName = "N/A";
                String workerRole = "N/A";

                if (workerObj instanceof Map) {
                    Map<?, ?> workerMap = (Map<?, ?>) workerObj;
                    workerName = workerMap.get("name") != null ? workerMap.get("name").toString() : "N/A";
                    workerRole = workerMap.get("role") != null ? workerMap.get("role").toString() : "N/A";
                }

                row.createCell(0).setCellValue(workerName);
                row.createCell(1).setCellValue(workerRole);
                row.createCell(2).setCellValue(task.getOrDefault("title", "N/A").toString());
                row.createCell(3).setCellValue(task.getOrDefault("description", "N/A").toString());
                row.createCell(4).setCellValue(task.getOrDefault("type", "N/A").toString());
                row.createCell(5).setCellValue(task.getOrDefault("priority", "N/A").toString());
                row.createCell(6).setCellValue(task.getOrDefault("dueTime", "N/A").toString());

                rowNum++;
            }

            // Auto-ajustar columnas
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            // Generar nombre de archivo
            String fileName = worker != null ?
                    "reporte_" + worker.replace(" ", "_") + ".xlsx" :
                    "reporte_todas_las_tareas.xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            workbook.write(response.getOutputStream());

            logger.info("Reporte generado exitosamente: {}", fileName);

        } catch (Exception e) {
            logger.error("Error generando reporte: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("Error generando reporte: " + e.getMessage());
        }
    }
}
