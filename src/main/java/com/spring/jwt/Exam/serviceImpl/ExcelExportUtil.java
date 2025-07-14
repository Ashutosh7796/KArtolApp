package com.spring.jwt.Exam.serviceImpl;

import com.spring.jwt.Exam.Dto.ExamSessionShowResultDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class ExcelExportUtil {

    public static InputStream exportExamSessionsToExcel(List<ExamSessionShowResultDto> sessions) throws Exception {
        String[] headers = {
                "Session ID", "User ID", "Student Name", "Paper ID", "Paper Title",
                "Student Class", "Start Time", "End Time", "Result Date",
                "Score", "Negative Count", "Negative Score"
        };

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ExamSessions");

        // Create header
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Fill data
        int rowNum = 1;
        for (ExamSessionShowResultDto dto : sessions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getSessionId());
            row.createCell(1).setCellValue(dto.getUserId() != null ? dto.getUserId() : 0);
            row.createCell(2).setCellValue(dto.getStudentName());
            row.createCell(3).setCellValue(dto.getPaperId());
            row.createCell(4).setCellValue(dto.getTitle());
            row.createCell(5).setCellValue(dto.getStudentClass());
            row.createCell(6).setCellValue(dto.getStartTime() != null ? dto.getStartTime().toString() : "");
            row.createCell(7).setCellValue(dto.getEndTime() != null ? dto.getEndTime().toString() : "");
            row.createCell(8).setCellValue(dto.getResultDate() != null ? dto.getResultDate().toString() : "");
            row.createCell(9).setCellValue(dto.getScore() != null ? dto.getScore() : 0);
            row.createCell(10).setCellValue(dto.getNegativeCount() != null ? dto.getNegativeCount() : 0);
            row.createCell(11).setCellValue(dto.getNegativeScore() != null ? dto.getNegativeScore() : 0);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convert workbook to InputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
