package model;

public class ProjectCustomer {
    private int projectId;
    private int customerId;

    public ProjectCustomer() {
    }

    public ProjectCustomer(int projectId, int customerId) {
        this.projectId = projectId;
        this.customerId = customerId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}

