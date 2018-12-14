package utils;

public class UtilsWorker extends Thread {
    final Process process;
	Integer exit;
	UtilsWorker(Process process) {
		this.process = process;
	}
	public void run() {
		try {
			exit = process.waitFor();
		} catch (InterruptedException ignore) {
			return;
		}
	}
}
