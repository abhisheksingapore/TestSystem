package me.veganbuddy.veganbuddy.actors;

import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getNumberOfAnimalsSaved;

/**
 * Created by abhishek on 24/11/17.
 */

public class AnimalsSavedDashboard {

    int cowSavedNum;
    int chickenSavedNum;
    int pigSavedNum;
    int fishSavedNum;
    int wildcatsSavedNum;
    int marinebykillSavedNum;

    int totalNumberOfAnimalsSavedNum;

    int cowSavedprogress;
    int chickenSavedprogress;
    int pigSavedprogress;
    int fishSavedprogress;
    int wildcatsSavedprogress;
    int marinebykillSavedprogress;

    public AnimalsSavedDashboard(int numberOfMeals, int veganDaysCount) {
        totalNumberOfAnimalsSavedNum = getNumberOfAnimalsSaved(numberOfMeals, this);
    }

    public int getCowSavedNum() {
        return cowSavedNum;
    }

    public void setCowSavedNum(int cowSavedNum) {
        this.cowSavedNum = cowSavedNum;
    }

    public int getChickenSavedNum() {
        return chickenSavedNum;
    }

    public void setChickenSavedNum(int chickenSavedNum) {
        this.chickenSavedNum = chickenSavedNum;
    }

    public int getPigSavedNum() {
        return pigSavedNum;
    }

    public void setPigSavedNum(int pigSavedNum) {
        this.pigSavedNum = pigSavedNum;
    }

    public int getFishSavedNum() {
        return fishSavedNum;
    }

    public void setFishSavedNum(int fishSavedNum) {
        this.fishSavedNum = fishSavedNum;
    }

    public int getWildcatsSavedNum() {
        return wildcatsSavedNum;
    }

    public void setWildcatsSavedNum(int wildcatsSavedNum) {
        this.wildcatsSavedNum = wildcatsSavedNum;
    }

    public int getMarinebykillSavedNum() {
        return marinebykillSavedNum;
    }

    public void setMarinebykillSavedNum(int marinebykillSavedNum) {
        this.marinebykillSavedNum = marinebykillSavedNum;
    }

    public int getCowSavedprogress() {
        return cowSavedprogress;
    }

    public void setCowSavedprogress(int cowSavedprogress) {
        this.cowSavedprogress = cowSavedprogress;
    }

    public int getChickenSavedprogress() {
        return chickenSavedprogress;
    }

    public void setChickenSavedprogress(int chickenSavedprogress) {
        this.chickenSavedprogress = chickenSavedprogress;
    }

    public int getPigSavedprogress() {
        return pigSavedprogress;
    }

    public void setPigSavedprogress(int pigSavedprogress) {
        this.pigSavedprogress = pigSavedprogress;
    }

    public int getFishSavedprogress() {
        return fishSavedprogress;
    }

    public void setFishSavedprogress(int fishSavedprogress) {
        this.fishSavedprogress = fishSavedprogress;
    }

    public int getWildcatsSavedprogress() {
        return wildcatsSavedprogress;
    }

    public void setWildcatsSavedprogress(int wildcatsSavedprogress) {
        this.wildcatsSavedprogress = wildcatsSavedprogress;
    }

    public int getMarinebykillSavedprogress() {
        return marinebykillSavedprogress;
    }

    public void setMarinebykillSavedprogress(int marinebykillSavedprogress) {
        this.marinebykillSavedprogress = marinebykillSavedprogress;
    }
}
