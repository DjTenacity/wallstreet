package com.yeepbank.android.base;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by WW on 2015/8/21.
 */
public class ActivityStacks {

    private Stack<Activity> stack;
    private static ActivityStacks activityStacks;
    private ActivityStacks(){
        if(stack == null){
            stack = new Stack<Activity>();
        }
    }

    public static ActivityStacks getInstances(){

        if(activityStacks == null){
            synchronized (ActivityStacks.class){
                activityStacks = new ActivityStacks();
            }
        }
        return activityStacks;
    }

    public void push(Activity activity){
        if(stack != null && !stack.contains(activity)){
            stack.push(activity);
        }
    }

    public void pop(Activity activity){
        if(stack != null && stack.contains(activity)){
            stack.remove(activity);
        }
    }

    public void pop(String activityName){
        for(Activity activity:stack){
            if(activity != null){
                if(activity.getClass().getName().equals(activityName)){
                    stack.remove(activity);
                    break;
                }
            }
        }
    }

    public void popToWitch(String activityName){
        for(Activity activity:stack){
            if(activity != null){
                if(!activity.getClass().getName().equals(activityName)){
                    activity.finish();
                }
            }
        }
    }

    public Activity getTop(){
        if (stack.size() > 0)
        return stack.get(stack.size() - 1);
        else
            return null;
    }

    public void clear(){
        if(stack != null && !stack.isEmpty()){
            stack.clear();
        }
    }

    public int getActivitySize(){
        return  stack.size();
    }

    public void finishActivity(){
        for(Activity activity:stack){
            if(activity != null){
                activity.finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public boolean hasActivity(String activityName){
        for(Activity activity:stack){
            if(activity != null){
                if(activity.getClass().getName().equals(activityName)){
                    return true;
                }
            }
        }
        return false;
    }
}
