import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeAnalyzer {

    // Date format used in the CSV file
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    // Assumption: CSV file is comma-separated
    private static final String CSV_DELIMITER = ",";

    public static void main(String[] args) {
        // Replace "your_file_path.csv" with the actual file path
        String filePath = "Assignment_Timecard.xlsx - Sheet1.csv";

        try {
            List<EmployeeRecord> employeeRecords = readCSV(filePath);

            // Analyze and print results
            analyzeAndPrintResults(employeeRecords);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static List<EmployeeRecord> readCSV(String filePath) throws IOException, ParseException {
        List<EmployeeRecord> employeeRecords = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                boolean firstLine = true;
                // Skip the first line (headers)
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] fields = line.split(CSV_DELIMITER);
                EmployeeRecord record = createEmployeeRecord(fields);
                if (record != null) {
                    employeeRecords.add(record);
                }
            }
        }

        return employeeRecords;
    }

    private static EmployeeRecord createEmployeeRecord(String[] fields) throws ParseException {
        // Assuming the CSV file structure is consistent
        if (fields.length == 9) {
            try {
            String positionId = fields[0].trim();
            String positionStatus = fields[1].trim();
             // Check for "Time" in date columns and skip them
             if ("Time".equalsIgnoreCase(fields[2]) || "Time".equalsIgnoreCase(fields[3])
             || "Time".equalsIgnoreCase(fields[5]) || "Time".equalsIgnoreCase(fields[6])) {
         return null;
     }
            Date timeIn = dateFormat.parse(fields[2].trim());
            Date timeOut = dateFormat.parse(fields[3].trim());
            int timecardHours = Integer.parseInt(fields[4].trim().split(":")[0]); // Assuming hours only
            Date payCycleStartDate = dateFormat.parse(fields[5].trim());
            Date payCycleEndDate = dateFormat.parse(fields[6].trim());
            String employeeName = fields[7].trim();
            String fileNumber = fields[8].trim();

            return new EmployeeRecord(positionId, positionStatus, timeIn, timeOut, timecardHours,
                    payCycleStartDate, payCycleEndDate, employeeName, fileNumber);
        } catch (ParseException | NumberFormatException e) {
            // Log the error or print a message for debugging
            System.err.println("Error parsing employee record: " + e.getMessage());
            return null;
        }
    }else {
            // Log or handle the case where the CSV row does not have the expected number of fields
            return null;
        }
    }

    private static void analyzeAndPrintResults(List<EmployeeRecord> employeeRecords) {
        for (int i = 0; i < employeeRecords.size(); i++) {
            EmployeeRecord currentRecord = employeeRecords.get(i);

            // Check for 7 consecutive days of work
            if (hasConsecutiveDaysWorked(employeeRecords, i, 7)) {
                System.out.println("Employee " + currentRecord.getEmployeeName() +
                        " has worked for 7 consecutive days as " + currentRecord.getPositionId());
            }

            // Check for less than 10 hours between shifts but greater than 1 hour
            if (i < employeeRecords.size() - 1) {
                EmployeeRecord nextRecord = employeeRecords.get(i + 1);
                long hoursBetweenShifts = calculateHoursBetween(currentRecord.getTimeOut(), nextRecord.getTimeIn());
                if (hoursBetweenShifts > 1 && hoursBetweenShifts < 10) {
                    System.out.println("Employee " + currentRecord.getEmployeeName() +
                            " has less than 10 hours between shifts as " + currentRecord.getPositionId());
                }
            }

            // Check for more than 14 hours in a single shift
            if (currentRecord.getTimecardHours() > 14) {
                System.out.println("Employee " + currentRecord.getEmployeeName() +
                        " has worked for more than 14 hours in a single shift as " + currentRecord.getPositionId());
            }
        }
    }

    private static boolean hasConsecutiveDaysWorked(List<EmployeeRecord> employeeRecords, int currentIndex, int consecutiveDays) {
        if (currentIndex + consecutiveDays - 1 < employeeRecords.size()) {
            Date startDate = employeeRecords.get(currentIndex).getPayCycleStartDate();
            Date endDate = employeeRecords.get(currentIndex + consecutiveDays - 1).getPayCycleEndDate();

            // Check if the days between start and end dates are consecutive
            long daysBetween = (endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
            return daysBetween == consecutiveDays - 1;
        }

        return false;
    }

    private static long calculateHoursBetween(Date start, Date end) {
        long milliseconds = end.getTime() - start.getTime();
        return milliseconds / (60 * 60 * 1000);
    }
}
class EmployeeRecord {
    private String positionId;
    private String positionStatus;
    private Date timeIn;
    private Date timeOut;
    private int timecardHours;
    private Date payCycleStartDate;
    private Date payCycleEndDate;
    private String employeeName;
    private String fileNumber;

    // Constructor
    public EmployeeRecord(String positionId, String positionStatus, Date timeIn, Date timeOut, int timecardHours,
                           Date payCycleStartDate, Date payCycleEndDate, String employeeName, String fileNumber) {
        this.positionId = positionId;
        this.positionStatus = positionStatus;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.timecardHours = timecardHours;
        this.payCycleStartDate = payCycleStartDate;
        this.payCycleEndDate = payCycleEndDate;
        this.employeeName = employeeName;
        this.fileNumber = fileNumber;
    }

    // Getters and setters

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getPositionStatus() {
        return positionStatus;
    }

    public void setPositionStatus(String positionStatus) {
        this.positionStatus = positionStatus;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public int getTimecardHours() {
        return timecardHours;
    }

    public void setTimecardHours(int timecardHours) {
        this.timecardHours = timecardHours;
    }

    public Date getPayCycleStartDate() {
        return payCycleStartDate;
    }

    public void setPayCycleStartDate(Date payCycleStartDate) {
        this.payCycleStartDate = payCycleStartDate;
    }

    public Date getPayCycleEndDate() {
        return payCycleEndDate;
    }

    public void setPayCycleEndDate(Date payCycleEndDate) {
        this.payCycleEndDate = payCycleEndDate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }
}

