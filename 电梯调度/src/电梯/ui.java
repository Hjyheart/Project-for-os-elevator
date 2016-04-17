package 电梯;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

/**
 * Created by hongjiayong on 16/4/10.
 */
public class ui {
    public static JLabel[] labels = new JLabel[20];
    public static JComboBox[] up = new JComboBox[20];
    public static JComboBox[] down = new JComboBox[20];
    public static JButton[] forOne = new JButton[20];
    public static JButton[] forTwo = new JButton[20];
    public static JButton[] forThree = new JButton[20];
    public static JButton[] forFour = new JButton[20];
    public static JButton[] forFive = new JButton[20];
    public static elevator one;
    public static elevator two;
    public static elevator three;
    public static elevator four;
    public static elevator five;

    public static ArrayList<elevator> elevators = new ArrayList<elevator>();
    public static ArrayList[][] queue = new ArrayList[20][2];
    public static boolean queLock[][] = new boolean[20][2];

    public static void init() {
        // 应答队列控制锁初始化
        for (int i = 0; i < 20; i++){
            queLock[i][0] = true;
            queLock[i][1] = true;
        }
        // 楼号初始化
        for (int i = 0; i < 20; i++){
            labels[i] = new JLabel(String.valueOf(i + 1));
            labels[i].setBackground(Color.WHITE);
            labels[i].setOpaque(true);
        }
        // 应答队列初始化
        for (int i = 0; i < 20; i++) {
            // 上升等待队列
            queue[i][0] = new ArrayList<Integer>();
            // 下降等待队列
            queue[i][1] = new ArrayList<Integer>();
        }
        // 上升选择键初始化
        for (int i = 0; i < 20; i++) {
            up[i] = new JComboBox();
            up[i].addItem("-");
            for(int k = i + 2; k <= 20; k++){
                up[i].addItem(String.valueOf(k));
            }
            final int finalI = i;
            up[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (ItemEvent.SELECTED == e.getStateChange()) {
                        queue[finalI][0].add(Integer.parseInt(up[finalI].getSelectedItem().toString()));
                        labels[finalI].setBackground(Color.GREEN);
                        // for test
                        System.out.println(queue[finalI][0]);
                    }
                }
            });
        }
        // 下降选择键初始化
        for (int i = 0; i < 20; i++) {
            down[i] = new JComboBox();
            down[i].addItem("-");
            for(int k = i; k > 0; k--){
                down[i].addItem(String.valueOf(k));
            }
            final int finalI = i;
            down[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (ItemEvent.SELECTED == e.getStateChange()) {
                        queue[finalI][1].add(Integer.parseInt(down[finalI].getSelectedItem().toString()));
                        labels[finalI].setBackground(Color.GREEN);
                        // for test
                        System.out.println(queue[finalI][1]);
                    }
                }
            });
        }
        // 电梯1初始化
        for (int i = 0; i < 20; i++){
            forOne[i] = new JButton("-");
            forOne[i].setOpaque(true);
            forOne[i].setBackground(Color.WHITE);
        }
        forOne[0].setBackground(Color.RED);
        // 电梯2初始化
        for (int i = 0; i < 20; i++){
            forTwo[i] = new JButton("-");
            forTwo[i].setOpaque(true);
            forTwo[i].setBackground(Color.WHITE);
        }
        forTwo[0].setBackground(Color.RED);
        // 电梯3初始化
        for (int i = 0; i < 20; i++){
            forThree[i] = new JButton("-");
            forThree[i].setOpaque(true);
            forThree[i].setBackground(Color.WHITE);
        }
        forThree[0].setBackground(Color.RED);
        // 电梯4初始化
        for (int i = 0; i < 20; i++){
            forFour[i] = new JButton("-");
            forFour[i].setOpaque(true);
            forFour[i].setBackground(Color.WHITE);
        }
        forFour[0].setBackground(Color.RED);
        // 电梯5初始化
        for (int i = 0; i < 20; i++){
            forFive[i] = new JButton("-");
            forFive[i].setOpaque(true);
            forFive[i].setBackground(Color.WHITE);
        }
        forFive[0].setBackground(Color.RED);
        JFrame frame = new JFrame("电梯");
        frame.setLayout(new GridLayout(21, 8));
        // 标签
        frame.add(new Label("楼层"));
        frame.add(new JLabel("上"));
        frame.add(new JLabel("下"));
        frame.add(new JLabel("电梯1"));
        frame.add(new JLabel("电梯2"));
        frame.add(new JLabel("电梯3"));
        frame.add(new JLabel("电梯4"));
        frame.add(new JLabel("电梯5"));
        // 装载
        for (int i = 20; i > 0; i--){
            frame.add(labels[i - 1]);
            frame.add(up[i - 1]);
            frame.add(down[i - 1]);
            for (int k = 0; k < 5; k++) {
                frame.add(forOne[i - 1]);
                frame.add(forTwo[i - 1]);
                frame.add(forThree[i - 1]);
                frame.add(forFour[i - 1]);
                frame.add(forFive[i - 1]);
            }
        }
        frame.pack();
        frame.setVisible(true);

        // 初始化电梯
        one = new elevator(1, 0, forOne);
        elevators.add(one);
        two = new elevator(2, 0, forTwo);
        elevators.add(two);
        three = new elevator(3, 0, forThree);
        elevators.add(three);
        four = new elevator(4, 0, forFour);
        elevators.add(four);
        five = new elevator(5, 0, forFive);
        elevators.add(five);
    }

    static class lightManger extends Thread{
        lightManger(){
            start();
        }
        public void run(){
            while (true) {
                for (int i = 0; i < 20; i++) {
                    if (queue[i][0].isEmpty() && queue[i][1].isEmpty()) {
                        labels[i].setBackground(Color.WHITE);
                    }
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class elevatorManager extends Thread{
        elevatorManager(){
            start();
        }

        public void adjust(int index, int i) throws InterruptedException {
            // 最优停滞电梯位于当前楼层下方
            if (elevators.get(index).getCurrentFloor()< i){
                elevators.get(index).setCurrentState(1);
                elevators.get(index).addUp(i);
                elevators.get(index).setMaxUp(i);
//                System.out.println(index + " " + i);
                Thread.sleep(500);
                return;
            }
            // 最优停滞电梯位于当前楼层上方
            if (elevators.get(index).getCurrentFloor()> i){
                elevators.get(index).setCurrentState(-1);
                elevators.get(index).addDown(i);
                elevators.get(index).setMinDown(i);
                Thread.sleep(500);
                return;
            }
            // 最优停滞电梯位于当前楼层
            if (elevators.get(index).getCurrentFloor() == i){
                elevators.get(index).setCurrentState(1);
                Thread.sleep(500);
                return;
            }
        }

        public void run(){
            while (true){
                for (int i = 0; i < 20; i++){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 监测上升队列
//                    System.out.println(queLock[i][0]);
                    while (!queLock[i][0]);
                    if (!queue[i][0].isEmpty()){
                        int index = -1,  distance = 1000000;
                        for (int k = 0; k < 5; k++){
                            if (elevators.get(k).getCurrentState() == 0 && !queue[i][0].isEmpty()){
                                if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
                                    index = k;
                                    distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
                                }
                            }
                            if (elevators.get(k).getCurrentFloor() >= i && elevators.get(k).getCurrentState() == -1
                                    && elevators.get(k).downMin() >= i){
                                if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
                                    index = -1;
                                    distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
                                }
                            }
                            if (elevators.get(k).getCurrentFloor() <= i && elevators.get(k).getCurrentState() == 1
                                    && elevators.get(k).upMax() >= i){
                                if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
                                    index = -1;
                                    distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
                                }
                            }
                        }
                        System.out.println(i + ":" + index);
                        if (index != -1 && !queue[i][0].isEmpty()){
                            try {
                                adjust(index, i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                for (int i = 0; i < 20; i++){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 监测下降队列
//                    System.out.println(queLock[i][1]);
                    while (!queLock[i][1]);
                    if (!queue[i][1].isEmpty()){
                        int index = -1,  distance = 1000000;
                        for (int k = 0; k < 5; k++){
                            if (elevators.get(k).getCurrentState() == 0 && !queue[i][1].isEmpty()){
                                if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
                                    index = k;
                                    distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
                                }
                            }
                            if (elevators.get(k).getCurrentFloor() >= i && elevators.get(k).getCurrentState() == -1
                                    && elevators.get(k).downMin() <= i){
                                if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
                                    index = -1;
                                    distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
                                }
                            }
                            if (elevators.get(k).getCurrentFloor() <= i && elevators.get(k).getCurrentState() == 1
                                    && elevators.get(k).upMax() <= i){
                                if (Math.abs(elevators.get(k).getCurrentFloor() - i) < distance){
                                    index = -1;
                                    distance = Math.abs(elevators.get(k).getCurrentFloor() - i);
                                }
                            }
                        }
//                        System.out.println(i + ":" + index);
                        if (index != -1 && !queue[i][1].isEmpty()){
                            try {
                                adjust(index, i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args){
        init();
        // 初始化灯管理
        lightManger lightmanger = new lightManger();
        elevatorManager elevatormanager = new elevatorManager();
        one.start();
        two.start();
        three.start();
        four.start();
        five.start();
    }
}
