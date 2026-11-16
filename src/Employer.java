public class Employer {

    private String companyName;
    private String companyAddress;
    private long employeeNumber;

    public Employer(String companyName, String companyAddress, long employeeNumber) {
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.employeeNumber = employeeNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public long getEmployerNumber() {
        return employeeNumber;
    }
}
