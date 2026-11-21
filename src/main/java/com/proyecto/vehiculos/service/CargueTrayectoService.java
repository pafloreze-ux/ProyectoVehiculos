package com.proyecto.vehiculos.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.proyecto.vehiculos.model.TrayectoTmp;
import com.proyecto.vehiculos.repository.TrayectoTmpRepository;

@Service
public class CargueTrayectoService {

    @Autowired
    private TrayectoTmpRepository trayectoTmpRepository;

    public Long procesarArchivoExcel(MultipartFile file) throws Exception {
        // Generar IdCargue numérico: AñoMesDiaHoraMinuto (ej. 202611201208)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        Long idCargue = Long.parseLong(timestamp);

        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            // Empezar en fila 1 (la fila 0 son los encabezados)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String idCond = formatter.formatCellValue(row.getCell(0));
                String placa = formatter.formatCellValue(row.getCell(1));
                String ruta = formatter.formatCellValue(row.getCell(2));
                String ubicacion = formatter.formatCellValue(row.getCell(3));
                String orden = formatter.formatCellValue(row.getCell(4));
                String lat = formatter.formatCellValue(row.getCell(5));
                String lon = formatter.formatCellValue(row.getCell(6));
                String usuario = formatter.formatCellValue(row.getCell(7));

                // 1. Invocar SP de Cargue
                trayectoTmpRepository.ejecutarCargue(idCond, placa, ruta, ubicacion, orden, lat, lon, usuario, idCargue);
            }
        }

        // 2. Invocar SP de Validación
        trayectoTmpRepository.ejecutarValidacion(idCargue);

        // 3. Invocar SP de Procesado
        trayectoTmpRepository.ejecutarProcesado(idCargue);

        return idCargue;
    }

    public List<TrayectoTmp> consultarPorIdCargue(Long idCargue) {
        return trayectoTmpRepository.findByIdCargue(idCargue);
    }
}