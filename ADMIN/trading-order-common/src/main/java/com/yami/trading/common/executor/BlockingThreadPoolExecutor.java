package com.yami.trading.common.executor;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/* 
 * 一个自定义的阻塞式线程池.
 * @author caster
 *
 **/ 
public class BlockingThreadPoolExecutor extends ThreadPoolExecutor {
	protected static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BlockingThreadPoolExecutor.class);
	
	private final ReentrantLock taskLock = new ReentrantLock();
	private final Condition unpaused = taskLock.newCondition();
	private final Condition submitCnd = taskLock.newCondition();
	private final int maxTaskCount;
	private volatile int currentTaskCount; // 正在执行的任务数量
	private volatile int activeTaskCount; // 已提交待执行的任务数量+正在执行的任务数量

	public BlockingThreadPoolExecutor(int fixedThreadPoolSize) {
		this(fixedThreadPoolSize, fixedThreadPoolSize, 60, TimeUnit.SECONDS);
	}
	
	public BlockingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
		// 经测试，好像是：正在执行的线程数量+等待队列的数量（容量还不能设置为0）=maximumPoolSize
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ArrayBlockingQueue<Runnable>(1), new AbortPolicy());
		this.maxTaskCount = maximumPoolSize + 1; //SynchronousQueue
	}

	@Override 
	public Future<?> submit(Runnable task) {
		taskLock.lock(); 
		try { 
			// 提交新任务时，如果发现线程池中所有任务数量（正在处理的+队列中待处理的）超过了允许的最大数量，则堵塞提交
			while (maxTaskCount <= activeTaskCount) {
				try {
					logger.warn("[BlockingThreadPoolExecutor submit] 线程池满了，无法执行当前任务, maxTaskCount:" + maxTaskCount + ", activeTaskCount:" + activeTaskCount);
					submitCnd.await();
				} catch (InterruptedException e) {
					throw new RuntimeException("线程池被中断...");
				} 
			}
		} finally { 
			taskLock.unlock(); 
		}

		// 内部提交请求，并且增加有效任务数量
		Future<?> submitResult = super.submit(task);
		activeTaskCount++;
		logger.info("[BlockingThreadPoolExecutor submit] 线程池添加了一个任务, maxTaskCount:" + maxTaskCount + ", activeTaskCount:" + activeTaskCount);
		return submitResult;
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		taskLock.lock(); 
		try {
			// 提交新任务时，如果发现线程池中所有任务数量（正在处理的+队列中待处理的）超过了允许的最大数量，则堵塞提交
			while (maxTaskCount <= activeTaskCount) { 
				try {
					logger.warn("[BlockingThreadPoolExecutor submit] 线程池满了，无法执行当前任务, maxTaskCount:" + maxTaskCount + ", activeTaskCount:" + activeTaskCount);
					submitCnd.await();
				} catch (InterruptedException e) {
					throw new RuntimeException("线程池被中断...");
				} 
			}
		} finally { 
			taskLock.unlock(); 
		}
		
		Future<T> submitResult = super.submit(task, result);
		activeTaskCount++;
		logger.info("[BlockingThreadPoolExecutor submit] 线程池添加了一个任务, maxTaskCount:" + maxTaskCount + ", activeTaskCount:" + activeTaskCount);
		return submitResult;
	}
	
	@Override
	public <T> Future<T> submit(Callable<T> task) {
		taskLock.lock(); 
		try {
			// 提交新任务时，如果发现线程池中所有任务数量（正在处理的+队列中待处理的）超过了允许的最大数量，则堵塞提交
			while (maxTaskCount <= activeTaskCount) { 
				try {
					logger.warn("[BlockingThreadPoolExecutor submit] 线程池满了，无法执行当前任务, maxTaskCount:" + maxTaskCount + ", activeTaskCount:" + activeTaskCount);
					submitCnd.await();
				} catch (InterruptedException e) {
					throw new RuntimeException("线程池被中断...");
				} 
			}
		} finally { 
			taskLock.unlock(); 
		}
		
		Future<T> submitResult = super.submit(task);
		activeTaskCount++;
		logger.info("[BlockingThreadPoolExecutor submit] 线程池添加了一个任务, maxTaskCount:" + maxTaskCount + ", activeTaskCount:" + activeTaskCount);
		return submitResult;
	}


	/**
	 * 选中一个线程开始处理当前业务的前置调用.
	 *
	 * @param t
	 * @param r
	 */
	@Override 
	protected void beforeExecute(Thread t, Runnable r) { 
		super.beforeExecute(t, r); 
		taskLock.lock(); 
		try { 
			// 正式处理的任务数量如果达到了允许的活跃线程池上限，则阻塞等待
			// 正常情况下不应该有此种情况出现，监控下日志
			while (maxTaskCount <= currentTaskCount + 1) {
				try {
					logger.warn("[BlockingThreadPoolExecutor beforeExecute] =======> maxTaskCount:{}, currentTaskCount:{}", maxTaskCount, currentTaskCount);
					unpaused.await();
				} catch (InterruptedException e) { 
					t.interrupt(); 
				} 
			} 
			currentTaskCount++; 
			logger.info("[BlockingThreadPoolExecutor beforeExecute] 有一个线程池任务开始正式执行, maxTaskCount:" + maxTaskCount + ", activeTaskCount:" + activeTaskCount + ", currentTaskCount:" + currentTaskCount);
		} finally {
			taskLock.unlock(); 
		} 
	}

	/**
	 * 一个线程完成业务处理后执行.
	 *
	 * @param r
	 * @param t
	 */
	@Override 
	protected void afterExecute(Runnable r, Throwable t) { 
		super.afterExecute(r, t); 
		taskLock.lock(); 
		try {
			// 正在执行的业务处理数减一
			currentTaskCount--;
			// 总的业务处理数量减一
			activeTaskCount--;

			logger.info("[BlockingThreadPoolExecutor afterExecute] 完成了一个线程池任务, currentTaskCount:" + currentTaskCount + ", activeTaskCount:" + activeTaskCount);

			// unpaused 可以考虑是否有必要
			unpaused.signalAll();
			// 有一个线程完成了业务处理，代表有空闲线程可以接受新的提交请求
			submitCnd.signalAll();
		} finally {
			taskLock.unlock(); 
		} 
	}

	/**
	 * 提前当前时刻正在执行中的任务数量(不包括处于等待队列的任务).
	 *
	 * @return
	 */
	public int getRunningTaskCount() {
		return this.currentTaskCount;
	}

	/**
	 * 获取正在执行的任务 + 队列中待执行的任务总数.
	 * 
	 * @return
	 */
	public int getUnFinishedTaskCount() {
		taskLock.lock();
		try {
			int count = this.currentTaskCount;
			count = count + this.getQueue().size();

			return count;
		} finally {
			taskLock.unlock();
		}
	}
} 
