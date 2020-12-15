package com.component;

import java.util.concurrent.Semaphore;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/14
 */
public class MySemaphore {
    public static Semaphore semaphore = new Semaphore(1);
}
