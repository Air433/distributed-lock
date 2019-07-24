package org.fate.cloud.distributed.lock.core;

import org.fate.cloud.distributed.lock.core.factory.DistributedFactory;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * @Author ouyanggang
 * @Date 2019/7/23 - 12:43
 */
public abstract class DistributedLock {

    protected String lockKey;

    protected String lockRequestId = UUID.randomUUID().toString()+Thread.currentThread().getId();

    protected volatile boolean isOpenExpirationRenewal = true;

    public abstract void lock();

    public abstract void unlock();

    protected abstract void renewalTime();

    public void setLockKey(String lockKey){
        this.lockKey = lockKey;
    }

    protected abstract void scheduleExpirationRenewal();

    protected void sleepBySecond(int second){
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void sleepBySecond(long expiredTiem){
        try {
            Thread.sleep(expiredTiem);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
