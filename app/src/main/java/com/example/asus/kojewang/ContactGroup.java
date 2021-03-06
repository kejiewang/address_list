package com.example.asus.kojewang;

import java.util.ArrayList;

/**
 * Created by asus on 2018/1/5.
 */

public class ContactGroup {
    String GroupName;
    long GroupId;
    ArrayList<ContactPerson> personList;

    public long getGroupId() {
        return GroupId;
    }

    public void setGroupId(long groupId) {

        GroupId = groupId;
    }

    ContactGroup(String groupName, long groupId, ArrayList<ContactPerson> personList){
        this.personList = new ArrayList<ContactPerson>();
        this.personList.addAll(personList);

        this.GroupName = groupName;
        this.GroupId = groupId;
    }
    ContactGroup(String groupName, long groupId){
        this.personList = new ArrayList<ContactPerson>();
        this.GroupName = groupName;
        this.GroupId = groupId;
    }
    ContactGroup()
    {
        this.personList = new ArrayList<ContactPerson>();
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public ArrayList<ContactPerson> getPersonList() {
        return personList;
    }

    public void setPersonList(ArrayList<ContactPerson> personList) {
        this.personList = personList;
    }
}
