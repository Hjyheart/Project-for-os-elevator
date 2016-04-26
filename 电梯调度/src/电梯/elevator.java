package 电梯;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by hongjiayong on 16/4/10.
 */

public class elevator extends Thread{
    /**
     * Elevator's attributes:
     * currentState: to show the three state(stop -> -1, on - > 0, up -> 1)
     * currentFloor: to show the current floor
     * currentMaxFloor: to show the max floor the elevator will stop
     * stopList: to store the floors which the elevator will stop
     */
    private int name;
    private int currentState;
    private int emerState;
    private int currentFloor;
    private int currentMaxFloor;
    private int maxUp;
    private int minDown;
    private Comparator<Integer> cmpUp = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    };
    private Comparator<Integer> cmpDown = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    };
    private Queue<Integer> upStopList = new PriorityQueue<Integer>(15, cmpUp);
    private Queue<Integer> downStopList = new PriorityQueue<Integer>(15, cmpDown);
    private JButton[] buttonList;

    elevator(int name, int dir, JButton[] buttonList){
        this.name = name;
        maxUp = 0;
        minDown = 19;
        currentState = dir;
        currentFloor = 0;
        currentMaxFloor = 0;
        emerState = -1;
        this.buttonList = buttonList;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        if(currentState == -2){
            emerState = this.currentState;
        }
        if(currentState == 2){
            currentState = emerState;
            emerState = -1;
        }
        this.currentState = currentState;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void popUp() {
        upStopList.poll();
    }

    public void addUp(Integer pos){
        upStopList.add(pos);
    }

    public void popDown(Integer pos){
        downStopList.poll();
    }

    public void addDown(Integer pos){
        downStopList.add(pos);
    }

    public int upMax(){return maxUp;}

    public void setMaxUp(int maxUp){this.maxUp = maxUp;}

    public int downMin(){return minDown;}

    public void setMinDown(int minDown){this.minDown = minDown;}

    public void run() {
        while(true){
            // 上升状态
            while (currentState == 1){
                boolean blueFlag = false;
                for (int i = 1; i < 20; i++){
                    buttonList[i].setText("上");
                }
                // 下客
                if (!upStopList.isEmpty() && currentFloor  == upStopList.peek()) {
                    while (currentFloor  == upStopList.peek()) {
                        Integer a = upStopList.poll();
                        ui.logs.append("电梯" + name + ": 第" + (currentFloor + 1) + "楼" + "下客\n");
                        if(upStopList.isEmpty())
                            break;
                    }
                    buttonList[currentFloor].setBackground(Color.BLUE);
                    blueFlag = true;
                }
                // 载上当前上升的人
                while (!ui.queLock[currentFloor][0]);
                ui.queLock[currentFloor][0] = false;
                if (!ui.queue[currentFloor][0].isEmpty()) {
                    for (int i = 0; i < ui.queue[currentFloor][0].size(); i++) {
                        if ((int) ui.queue[currentFloor][0].get(i) - 1 > maxUp) {
                            maxUp = (int) ui.queue[currentFloor][0].get(i) - 1;
                        }
                        addUp((Integer) ui.queue[currentFloor][0].get(i) - 1);
                        ui.logs.append("电梯" + name + ": 第" + (currentFloor + 1) + "楼载上去" + ui.queue[currentFloor][0].get(i)
                        + "楼的乘客\n");
                    }
                    buttonList[currentFloor].setBackground(Color.BLUE);
                    blueFlag = true;
                }
                ui.queue[currentFloor][0].clear();
                ui.queLock[currentFloor][0] = true;
                // 电梯走空 载上向下的人
                while (!ui.queLock[currentFloor][1]);
                ui.queLock[currentFloor][1] = false;
                if (upStopList.isEmpty() && !ui.queue[currentFloor][1].isEmpty()){
                    for (int i = 0; i < ui.queue[currentFloor][1].size();i++){
                        if ((int)ui.queue[currentFloor][1].get(i) - 1 < minDown){
                            minDown = (int)ui.queue[currentFloor][1].get(i) - 1;
                        }
                        addDown((Integer) ui.queue[currentFloor][1].get(i) - 1);
                        ui.logs.append("电梯" + name + ": 第" + (currentFloor + 1) + "楼载上去" + ui.queue[currentFloor][1].get(i)
                                + "楼的乘客\n");
                    }
                    if (!downStopList.isEmpty()){
                        ui.queue[currentFloor][1].clear();
                        setCurrentState(-1);
                        blueFlag = true;
                        ui.queLock[currentFloor][1] = true;
                        ui.logs.append("电梯" + name + " :开始下降\n");
                        break;
                    }
                }
                ui.queLock[currentFloor][1] = true;

                if (blueFlag){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    buttonList[currentFloor].setBackground(Color.RED);
                }
                // 电梯空了 到顶了
                if (upStopList.isEmpty() || currentFloor == 19){
                    setCurrentState(0);
                    maxUp = 0;
                    minDown = 19;
                    buttonList[currentFloor].setBackground(Color.RED);
                    ui.logs.append("电梯" + name + ": 停止运作\n");
                    break;
                }
                buttonList[currentFloor].setBackground(Color.WHITE);
                currentFloor++;
                buttonList[currentFloor].setBackground(Color.RED);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 下降状态
            while(currentState == -1){
                boolean blueFlag = false;
                for (int i = 1; i < 20; i++){
                    buttonList[i].setText("下");
                }
                // 下客
                if (!downStopList.isEmpty() && currentFloor  == downStopList.peek()) {
                    System.out.println(downStopList.peek());
                    while (currentFloor  == downStopList.peek()) {
                        Integer a = downStopList.poll();
                        ui.logs.append("电梯" + name + ": 第" + (currentFloor + 1) + "楼" + "下客\n");
                        if(downStopList.isEmpty())
                            break;
                    }
                    buttonList[currentFloor].setBackground(Color.BLUE);
                    blueFlag = true;
                }
                // 载上当前下降的人
                while (!ui.queLock[currentFloor][1]);
                ui.queLock[currentFloor][1] = false;
                if (!ui.queue[currentFloor][1].isEmpty()) {
                    for (int i = 0; i < ui.queue[currentFloor][1].size(); i++) {
                        if ((int) ui.queue[currentFloor][1].get(i) - 1 < minDown) {
                            minDown = (int) ui.queue[currentFloor][1].get(i) - 1;
                        }
                        addDown((Integer) ui.queue[currentFloor][1].get(i) - 1);
                        ui.logs.append("电梯" + name + ": 第" + (currentFloor + 1) + "楼载上去" + ui.queue[currentFloor][1].get(i)
                                + "楼的乘客\n");
                    }
                    buttonList[currentFloor].setBackground(Color.BLUE);
                    blueFlag = true;
                }
                ui.queue[currentFloor][1].clear();
                ui.queLock[currentFloor][1] = true;

                // 电梯走空 载上向上的人
                while (!ui.queLock[currentFloor][0]);
                ui.queLock[currentFloor][0] = false;
                if (downStopList.isEmpty() && !ui.queue[currentFloor][0].isEmpty()){
                    for (int i = 0; i < ui.queue[currentFloor][0].size();i++){
                        if ((int)ui.queue[currentFloor][0].get(i) - 1 > maxUp){
                            maxUp = (int)ui.queue[currentFloor][0].get(i) - 1;
                        }
                        addUp((Integer) ui.queue[currentFloor][0].get(i) - 1);
                        ui.logs.append("电梯" + name + ": 第" + (currentFloor + 1) + "楼载上去" + ui.queue[currentFloor][0].get(i)
                                + "楼的乘客\n");
                    }
                    if (!upStopList.isEmpty()){
                        ui.queue[currentFloor][0].clear();
                        setCurrentState(1);
                        blueFlag = true;
                        ui.queLock[currentFloor][0] = true;
                        ui.logs.append("电梯" + name + " :开始上升\n");
                        break;
                    }
                }
                ui.queLock[currentFloor][0] = true;
                if (blueFlag){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    buttonList[currentFloor].setBackground(Color.RED);
                }
                // 电梯走空 到底
                if (downStopList.isEmpty() || currentFloor == 0){
                    buttonList[currentFloor].setBackground(Color.RED);
                    setCurrentState(0);
                    maxUp = 0;
                    minDown = 19;
                    ui.logs.append("电梯" + name + ": 停止运作\n");
                    break;
                }
                buttonList[currentFloor].setBackground(Color.WHITE);
                currentFloor--;
                buttonList[currentFloor].setBackground(Color.RED);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 停滞状态
            while(currentState == 0){
                for (int i = 1; i < 20; i++){
                    buttonList[i].setText("-");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 防止线程阻塞
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
