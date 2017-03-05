package model;


public class SkillsDeveloper {
    private int developerId;
    private int skillsId;

    public SkillsDeveloper() {
    }

    public SkillsDeveloper(int developerId, int skillsId) {
        this.developerId = developerId;
        this.skillsId = skillsId;
    }

    public int getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(int developerId) {
        this.developerId = developerId;
    }

    public int getSkillsId() {
        return skillsId;
    }

    public void setSkillsId(int skillsId) {
        this.skillsId = skillsId;
    }
}

