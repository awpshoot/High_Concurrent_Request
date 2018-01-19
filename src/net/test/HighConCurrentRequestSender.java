package net.test;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class HighConCurrentRequestSender {
	
	private static int thread_num = 16;   
	private static int client_num = 8;

	
	public static void man(String[] args) {
		ExecutorService exec = Executors.newCachedThreadPool();
		final Semaphore semp = new Semaphore(thread_num);
		for(int index = 0; index < client_num; index++){
			final int NO = index;
			Runnable run = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try{
						semp.acquire();
						String host = "http://127.0.0.1:8080";
						String para = "pq=pq";
						URL url = new URL(host);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("POST");
						connection.setUseCaches(false);
						//是否自动执行重定向,默认为true  
						connection.setInstanceFollowRedirects(true);
						//配置本次连接的Content-type，配置为application/x-www-form-urlencoded的意思是正文是urlencoded编码过的form参数  
						//下面我们可以看到我们对正文内容使用URLEncoder.encode 进行编码  
						connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
						connection.setRequestProperty("Charset", "UTF-8");
						connection.setRequestProperty("Connection", "Keep-Alive");
						connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
						connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
						connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
						connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
						// connection.setRequestProperty("Proxy-Connection","Keep-Alive");
						// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在   
						// http正文内，因此需要设为true, 默认情况下是false;
						connection.setDoOutput(true);
						connection.setDoInput(true);
						PrintWriter out = new PrintWriter(connection.getOutputStream());
						out.print(para);
						out.flush();
						out.close();
						int result = connection.getResponseCode();
						System.out.println(NO+"------"+result);
						semp.release();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			};
			exec.execute(run);
		}
	}
	
	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool(); //创建一个线程池
		final CountDownLatch cdOrder = new CountDownLatch(1);//指挥官的命令，设置为1，指挥官一下达命令，则cutDown,变为0，战士们执行任务
		final CountDownLatch cdAnswer = new CountDownLatch(3);//因为有三个战士，所以初始值为3，每一个战士执行任务完毕则cutDown一次，当三个都执行完毕，变为0，则指挥官停止等待。
		Runnable runnable = new Runnable(){
			public void run(){
				try {
					System.out.println("线程" + Thread.currentThread().getName() +
							"正准备接受命令");
					cdOrder.await(); //战士们都处于等待命令状态
					System.out.println("线程" + Thread.currentThread().getName() +
							"已接受命令");
					Thread.sleep((long)(Math.random()*10000));
					System.out.println("线程" + Thread.currentThread().getName() +
							"回应命令处理结果");

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cdAnswer.countDown(); //任务执行完毕，返回给指挥官，cdAnswer减1。
				}
			}
		};
		for(int i=0;i<3;i++){
			
			service.execute(runnable);//为线程池添加任务
		}
		try {
			Thread.sleep((long)(Math.random()*10000));

			System.out.println("线程" + Thread.currentThread().getName() +
					"即将发布命令");
			cdOrder.countDown(); //发送命令，cdOrder减1，处于等待的战士们停止等待转去执行任务。
			System.out.println("线程" + Thread.currentThread().getName() +
					"已发送命令，正在等待结果");
			cdAnswer.await(); //命令发送后指挥官处于等待状态，一旦cdAnswer为0时停止等待继续往下执行
			System.out.println("线程" + Thread.currentThread().getName() +
					"已收到所有响应结果");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		service.shutdown(); //任务结束，停止线程池的所有线程
	}

	
}



