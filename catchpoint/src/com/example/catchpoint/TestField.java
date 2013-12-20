package com.example.catchpoint;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

public class TestField extends SurfaceView implements SensorEventListener, Callback {
	private Context context;
	private int canvasWidth;
	private int canvasHeight;
	private float canvasRatio;
	private Paint color;
	private Sensor sensor;
	private List<Point> points;
	private long lastSensorUpdate = 0;
	private static final float DEADZONE = 0.1F; // in m/s^2
	private static final int REFRESH_RATE = 10; // in ms
	private static final float SPEED_DIVISOR = 100/REFRESH_RATE;
	private boolean isInitialized = false;
	private FieldThread fieldThread;

	public TestField(Context context) {
		super(context);
		this.context = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		points = new ArrayList<Point>();
		Point dummy1 = new Point(0, 50, 50, 2, Color.YELLOW);
		Point dummy2 = new Point(1, 20, 20, 1.5F, Color.MAGENTA);
		points.add(dummy1);
		points.add(dummy2);

		SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		color = new Paint();
		color.setColor(Color.GREEN);
	}

	public void doDraw(Canvas canvas) {
		draw(canvas);
		if(!isInitialized){
			this.canvasWidth = canvas.getWidth();
			this.canvasHeight = canvas.getHeight();
			this.canvasRatio = canvasWidth/canvasHeight;
			this.isInitialized = true;
		}
		for (int i = 0; i < points.size(); i++){
			canvas.drawCircle(percentWidth(points.get(i).getX()), percentHeight(points.get(i).getY()), percentWidth(points.get(i).getRadius()), points.get(i).getColor());
		}
	}

	private int percentWidth(float percent) {
		return Math.round(canvasWidth / 100F * percent);
	}

	private int percentHeight(float percent) {
		return Math.round(canvasHeight / 100F * percent);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	public static float[] adjustAccelOrientation(int displayRotation, float[] eventValues) 
	{ 
	    float[] adjustedValues = new float[2];

	    final int axisSwap[][] = {
	    { 1, -1,  0,  1},	// ROTATION_0 
	    {-1, -1,  1,  0},	// ROTATION_90 
	    {-1,  1,  0,  1},	// ROTATION_180 
	    { 1,  1,  1,  0} };	// ROTATION_270 

	    final int[] as = axisSwap[displayRotation]; 
	    adjustedValues[0]  =  (float)as[0] * eventValues[ as[2] ]; 
	    adjustedValues[1]  =  (float)as[1] * -eventValues[ as[3] ];

	    return adjustedValues;
	}
	
	public void movePoint(Point p, float values[]){
		float eventX = values[0];
		float eventY = values[1];
		
		if(Math.abs(eventY) > DEADZONE || Math.abs(eventX) > DEADZONE) {
			if(eventX < -DEADZONE){
				eventX = Math.abs(eventX)/SPEED_DIVISOR;
				p.setX((p.getX() + eventX) > 100 ? (p.getX() + eventX - 100) : (p.getX() + eventX));
				color.setColor(Color.GREEN);
			}
			else if(eventX > DEADZONE){
				eventX = Math.abs(eventX)/SPEED_DIVISOR;
				p.setX((p.getX() - eventX) < 0 ? (p.getX() - eventX + 100) : (p.getX() - eventX));
				color.setColor(Color.BLUE);
			}
			if(eventY > DEADZONE){
				eventY = Math.abs(eventY)/SPEED_DIVISOR*canvasRatio;
				p.setY((p.getY() + eventY) > 100 ? (p.getY() + eventY - 100) : (p.getY() + eventY));
				color.setColor(Color.MAGENTA);
			}
			else if(eventY < -DEADZONE){
				eventY = Math.abs(eventY)/SPEED_DIVISOR*canvasRatio;
				p.setY((p.getY() - eventY) < 0 ? (p.getY() - eventY + 100) : (p.getY() - eventY));
				color.setColor(Color.RED);
			}
			invalidate();
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		long curTime = System.currentTimeMillis();
		if(curTime - lastSensorUpdate > REFRESH_RATE) {
			lastSensorUpdate = curTime;
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			float values[] = adjustAccelOrientation(wm.getDefaultDisplay().getRotation(), event.values);
			for(int i = 0; i < points.size(); i++){
				movePoint(points.get(i), values);
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		fieldThread = new FieldThread(holder, context, this);
		fieldThread.setRunning(true);
		fieldThread.start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		fieldThread.setRunning(false);
		boolean retry = true;
		while(retry){
			try	{
				fieldThread.join();
				retry = false;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
