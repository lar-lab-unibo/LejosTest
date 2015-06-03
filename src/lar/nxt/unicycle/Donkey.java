package lar.nxt.unicycle;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Settings;
import lejos.nxt.SystemSettings;
import lejos.util.Matrix;

public class Donkey implements Runnable {

	protected double radius = 0.0;
	protected double interaxis = 0.0;
	protected double b_distance = 0.0;

	protected Matrix D;
	protected Matrix C;
	protected Matrix A;

	protected double theta = 0.0;
	protected Matrix p_goal;
	protected Matrix p;
	protected Matrix p_dot;

	protected Matrix omegas;

	
	public double K_OMEGA = 0.2* 180.0/Math.PI;
	public double K_ATT = 20.0/K_OMEGA;

	protected NXTRegulatedMotor left_motor;
	protected NXTRegulatedMotor right_motor;

	
	public boolean alive = true;
	public Thread mainThread;
	
	/**
	 * Costruisce il Mulo
	 * 
	 * @param radius
	 *            raggio ruota
	 * @param interaxis
	 *            distanza interasse
	 * @param b_distance
	 *            distanza punt B
	 */
	public Donkey(NXTRegulatedMotor left_motor, NXTRegulatedMotor right_motor,
			double radius, double interaxis, double b_distance) {
		
		this.left_motor = left_motor;
		this.right_motor = right_motor;
		
		this.radius = radius;
		this.interaxis = interaxis;
		this.b_distance = b_distance;

		this.D = new Matrix(2, 2);
		this.C = new Matrix(2, 2);

		this.p = new Matrix(2, 1);
		this.p_dot = new Matrix(2, 1);
		this.p_goal = new Matrix(2, 1);
		this.omegas = new Matrix(2, 1);

		this.D.set(0, 0, this.radius / 2);
		this.D.set(0, 1, this.radius / 2);
		this.D.set(1, 0, this.radius / this.interaxis);
		this.D.set(1, 1, -this.radius / this.interaxis);
		
		//Initialization
		updateOdometry(0, 0, 0);
		setGoal(0, 0, 0);
		
		mainThread = new Thread(this);
		mainThread.start();
	}

	/**
	 * Aggiorna matrice A in base al Theta del robot
	 * 
	 * @param theta
	 */
	public synchronized void updateOdometry(double x, double y, double theta) {
		p.set(0, 0, x);
		p.set(1, 0, y);
		this.theta = theta;
	}

	/**
	 * Setta la posizione del Goal
	 * 
	 * @param x
	 * @param y
	 * @param theta
	 */
	public synchronized void setGoal(double x, double y, double theta)  {
		this.p_goal.set(0, 0, x);
		this.p_goal.set(1, 0, y);
	}

	/**
	 * Aggiorna lo stato del robot
	 */
	public void updateTime(){
		long t1 = System.currentTimeMillis();
		C.set(0, 0, Math.cos(theta));
		C.set(0, 1, -this.b_distance * Math.sin(theta));
		C.set(1, 0, Math.sin(theta));
		C.set(1, 1, this.b_distance * Math.cos(theta));

		A = C.times(D);
		
		p_dot = p.minus(p_goal).times(-K_ATT);
		omegas = A.inverse().times(p_dot).times(K_OMEGA);
		long t2 = System.currentTimeMillis();
		LCD.drawString("ms:"+(t2-t1), 0, 6);
	}

	
	/**
	 * Applica le Omega ai motori con le dovute precauzioni di direzione
	 */
	public synchronized void actuatesMotors(){
		int w_r = (int)omegas.get(0, 0);
		int w_l = (int)omegas.get(1, 0);
		
		left_motor.setSpeed(w_l);
		right_motor.setSpeed(w_r);
		
		if(w_l>=0){
			left_motor.forward();
		}else{
			left_motor.backward();
		}
		
		if(w_r>=0){
			right_motor.forward();
		}else{
			right_motor.backward();
		}
	}

	/**
	 * Ciclo principale
	 */
	@Override
	public void run() {
		while(alive){
			updateTime();
			actuatesMotors();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
