package com.grouper.models;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SkillSet {

    public static final String JAVA_SKILL_NAME = "Java";
    public static final String C_SKILL_NAME = "C";
    public static final String CPP_SKILL_NAME = "C++";
    public static final String OBJC_SKILL_NAME = "Obj-C";
    public static final String SWIFT_SKILL_NAME = "Swift";
    public static final String PYTHON_SKILL_NAME = "Python";
    public static final String HTML_SKILL_NAME = "HTML";
    public static final String JAVASCRIPT_SKILL_NAME = "Javascript";
    public static final String NESTED_SKILLS_KEY = "skills";

    private HashMap<String, Boolean> skills;

    public SkillSet() {
        HashMap<String, Boolean> skills = new HashMap<>();
        this.skills = skills;

        // TODO - add more skills
        setAllSkillsFalse();
    }

    public SkillSet(HashMap<String, Boolean> skills) {
        this.skills = skills;
    }

    private void setAllSkillsFalse() {
        ArrayList<String> skillNames = new ArrayList<>();
        skillNames.add(JAVA_SKILL_NAME);
        skillNames.add(C_SKILL_NAME);
        skillNames.add(CPP_SKILL_NAME);
        skillNames.add(OBJC_SKILL_NAME);
        skillNames.add(SWIFT_SKILL_NAME);
        skillNames.add(PYTHON_SKILL_NAME);
        skillNames.add(HTML_SKILL_NAME);
        skillNames.add(JAVASCRIPT_SKILL_NAME);

        for (String name : skillNames) {
            this.skills.put(name, Boolean.FALSE);
        }
    }

    public void addSkill(String skillName) {
        this.skills.replace(skillName, Boolean.TRUE);
    }

    public void removeSkill(String skillName) {
        this.skills.replace(skillName, Boolean.FALSE);
    }

    public HashMap<String, Boolean> getSkills() {
        return skills;
    }

    public Map<String, AttributeValue> toAttributeValue() {
        HashMap<String, AttributeValue> attributeValueMap = new HashMap<>();

        Set<String> keySet = this.skills.keySet();
        for (String key : keySet) {
            attributeValueMap.put(key, new AttributeValue()
                .withBOOL(skills.get(key)));
        }

        return attributeValueMap;
    }

    public static HashMap<String, Boolean> extractSkillSet(Map<String, AttributeValue> attributeValueMap) {
        HashMap<String, Boolean> userSkillSet = new HashMap<>();

        for (Map.Entry<String, AttributeValue> entry : attributeValueMap.entrySet()) {
            userSkillSet.put(entry.getKey(), entry.getValue().getBOOL());
        }

        return userSkillSet;
    }

}
