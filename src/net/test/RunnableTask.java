package net.test;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class RunnableTask implements Runnable{
	volatile static boolean bol = false;
	volatile static boolean exit = false;
	//number仅测试用完了可以删掉
	final AtomicInteger number = new AtomicInteger();
	volatile String threadName = null;
	@Override
	public void run() {
		threadName = Thread.currentThread().getName();
		System.out.println(number.getAndIncrement());
		synchronized (this) {
			try {
				if (!bol) {
					bol = true;
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(!exit){
			//此处放置测试逻辑代码
			//System.out.println(Thread.currentThread().getName() + "并发数量为" + number.intValue());
			//String retMsg = PubClientToService.sendToService(objs, url, method, nameSpace);
		}
	}
	
	
	public static void main(String[] args) {
		final ExecutorService pool = Executors.newCachedThreadPool();
		final RunnableTask task = new RunnableTask();
		HashMap<String, String> threadMap = new HashMap<>();
		String threadName = null;
		//int num = Integer.parseInt(args[0]);  //此处用来设置循环i次的值为num
		//int min = Integer.parseInt(args[1]);  //此处用来设置几分钟放在计时任务中min*60*1000
		for (int i=0;i<5;i++) {
			pool.execute(task);
			int randomNum = (int)(Math.random()*1000000);
			//此处设置线程名称T开头，加两位线程号i，加当前时间，加六位随机数。放在threadMap中。
			System.out.println("T" + String.format("%02d", i)
					+ System.currentTimeMillis() + String.format("%06d", randomNum));
			threadName = "T" + String.format("%02d", i)
					+ System.currentTimeMillis() + String.format("%06d", randomNum);
			//threadName = Thread.currentThread().getName();
		}
		TimerTask tmt = new TimerTask() {
			@Override
			public void run() {
				task.exit = true;
			}
		};
		Timer tm = new Timer();
		tm.schedule(tmt, 1*1000*60);
	}
}
