package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.AttendanceDTO;
import com.LongChau.HealthMateLC.repository.TimesheetRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private TimesheetRepository timesheetRepository;

    public List<AttendanceDTO> getAttendanceByPharmacyAndDateRange(Integer pharmacyId, LocalDate startDate, LocalDate endDate) {
        return timesheetRepository.findAttendanceByPharmacyAndDateRange(pharmacyId, startDate, endDate);
    }

    public byte[] generateAttendanceExcel(List<AttendanceDTO> attendanceList, String pharmacyName,
                                          LocalDate startDate, LocalDate endDate) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Đổi từ dd/MM/yyyy thành dd-MM-yyyy
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String sheetName = "Bang cham cong " + startDate.format(dateFormatter) + " den " + endDate.format(dateFormatter);
            // Đảm bảo tên sheet không quá 31 ký tự (giới hạn của Excel)
            if (sheetName.length() > 31) {
                sheetName = "Bang cham cong";
            }
            Sheet sheet = workbook.createSheet(sheetName);

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Sửa lại phần create title và period row
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Create title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BẢNG CHẤM CÔNG - " + pharmacyName.toUpperCase());
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

            // Create period row
            Row periodRow = sheet.createRow(1);
            Cell periodCell = periodRow.createCell(0);
            periodCell.setCellValue("Từ ngày: " + startDate.format(displayFormatter) +
                    " đến ngày: " + endDate.format(displayFormatter));
            CellStyle periodStyle = workbook.createCellStyle();
            Font periodFont = workbook.createFont();
            periodFont.setFontHeightInPoints((short) 12);
            periodStyle.setFont(periodFont);
            periodStyle.setAlignment(HorizontalAlignment.CENTER);
            periodCell.setCellStyle(periodStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 5));

            // Create header row
            Row headerRow = sheet.createRow(3);
            String[] headers = {"STT", "Tên nhân viên", "Ngày", "Giờ vào", "Giờ ra", "Tổng giờ làm"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 4;
            int stt = 1;

            for (AttendanceDTO attendance : attendanceList) {
                Row dataRow = sheet.createRow(rowNum++);

                // STT
                Cell sttCell = dataRow.createCell(0);
                sttCell.setCellValue(stt++);
                sttCell.setCellStyle(dataStyle);

                // Tên nhân viên
                Cell nameCell = dataRow.createCell(1);
                nameCell.setCellValue(attendance.getFullName() != null ? attendance.getFullName() : "N/A");
                nameCell.setCellStyle(dataStyle);

                // Ngày
                Cell dateCell = dataRow.createCell(2);
                dateCell.setCellValue(attendance.getDate() != null ?
                        attendance.getDate().format(dateFormatter) : "N/A");
                dateCell.setCellStyle(dataStyle);

                // Giờ vào
                Cell checkinCell = dataRow.createCell(3);
                checkinCell.setCellValue(attendance.getCheckin() != null ?
                        attendance.getCheckin().format(timeFormatter) : "N/A");
                checkinCell.setCellStyle(dataStyle);

                // Giờ ra
                Cell checkoutCell = dataRow.createCell(4);
                checkoutCell.setCellValue(attendance.getCheckout() != null ?
                        attendance.getCheckout().format(timeFormatter) : "Chưa checkout");
                checkoutCell.setCellStyle(dataStyle);

                // Tổng giờ làm
                Cell totalHoursCell = dataRow.createCell(5);
                totalHoursCell.setCellValue(attendance.getTotalHours());
                totalHoursCell.setCellStyle(dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Set minimum width
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}