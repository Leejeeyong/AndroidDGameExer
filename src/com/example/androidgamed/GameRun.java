package com.example.androidgamed;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

public class GameRun extends SurfaceView implements Callback {
	private GameThread m_GameThread;
	private CMopMgr m_cMopMgr = new CMopMgr();
	
	class CMop{
		public int m_x;
		public int m_y;
		public long m_beforeTime;
		public long m_Sleep;
		public int m_Speed;
		public int m_MoveArea;
		public int m_MopHealth;
		public boolean m_Used;
		public boolean m_Die;
		public int m_Direction;
		
		public int m_MovePos[][]={
				{235,0,2},
				{235,95,3},
				{70,95,2},
				{70,600,1},
				{230,600,4},
				{230,400,1},
				{480,400,2},
				{480,890,3},
				{75,890,2},
				{75,1170,1},
				{650,1170,4},
				{650,110,3},
				{415,110,4},
				{415,0,0}
		};
		
		CMop(){
			m_x=235;
			m_y=0;
			m_beforeTime=System.currentTimeMillis();
			m_Sleep=10;
			m_Speed=5;
			m_MoveArea=1;
			m_Used=false;
			m_MopHealth=100;
			m_Die=false;
			m_Direction=-1;
		}
		
		public void MovePosition(){
			if(System.currentTimeMillis()-m_beforeTime > m_Sleep){
				m_beforeTime=System.currentTimeMillis();
			}
			else{
				return;
			}
			
			int nDir=m_MovePos[m_MoveArea-1][2];
			if(m_Direction != nDir){
				m_Direction=nDir;
			}
			
			if(m_MoveArea>13){
				m_Direction=-1;
				m_x=235;
				m_y=0;
				m_cMopMgr.m_OverCount += 1;
				m_MoveArea=1;
				return;
			}
			
			if(nDir == 1 ){
				m_x += m_Speed;
				if(m_MovePos[m_MoveArea][0]<m_x){m_MoveArea++;}
			}
			else if(nDir==2){
				m_y += m_Speed;
				if(m_MovePos[m_MoveArea][0]<m_x){m_MoveArea++;}
			}
			else if(nDir == 3){
				m_x -= m_Speed;
				if(m_MovePos[m_MoveArea][0]>m_x){m_MoveArea--;}
			}
			else if(nDir==4){
				m_y -= m_Speed;
				if(m_MovePos[m_MoveArea][0]>m_x){m_MoveArea--;}
			}
			
		}
	}
	
	
	class CMopMgr{
		public static final int m_MopCnt = 20;
		public CMop mop[] = new CMop[m_MopCnt];
		public int m_UsedMopCnt = 0;
		public int m_DieMopCnt = 0;
		public long m_Regen = 1000;
		public int m_OverCount = 0;
		public long m_BeforeRegen = System.currentTimeMillis();
		CMopMgr(){
			for(int n=0;n<m_MopCnt;n++){
				mop[n]=new CMop();
			}
		}
		
		
		public void AddMop(){
			if(System.currentTimeMillis()-m_BeforeRegen > m_Regen){
				m_BeforeRegen = System.currentTimeMillis();
			}
			else return;
			
			if(m_UsedMopCnt >= (m_MopCnt-1)){return;}
			mop[m_UsedMopCnt].m_Used=true;
			
			m_UsedMopCnt++;
		}
		
		public void MoveMop(){
			for(int n=0;n<m_MopCnt;n++){
				if(mop[n].m_Used==true){
					mop[n].MovePosition();
				}
			}
		}
		
		
		
		
	}
	
	class GameThread extends Thread{
		private int m_DisplayWidth;
		private int m_DisplayHeight;
		private Bitmap m_bmpBg;
		private Bitmap m_bmpMop;
		private Bitmap m_bmpMissile;
		private Bitmap m_bmpTower;
		private SurfaceHolder m_SurfaceHolder;
		public boolean m_Stop;
		
		public GameThread(SurfaceHolder surfaceholder, Context context){
			m_SurfaceHolder = surfaceholder;
			Resources res = context.getResources();
			
			m_bmpBg=BitmapFactory.decodeResource(res, R.drawable.bg);
			m_bmpMop=BitmapFactory.decodeResource(res, R.drawable.mop);
			m_bmpMissile=BitmapFactory.decodeResource(res, R.drawable.missile);
			m_bmpTower=BitmapFactory.decodeResource(res, R.drawable.tower);
			
			Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
			
			m_DisplayWidth=display.getWidth();
			m_DisplayHeight=display.getHeight();
			
			m_Stop=false;
		}
		
		public void run(){
			while(!m_Stop){
				Canvas canvas =null;
				try{
					canvas = m_SurfaceHolder.lockCanvas(null);
					synchronized(m_SurfaceHolder){
						Rect rcSrc = new Rect();
						Rect rcDest = new Rect();
						
						rcSrc.set(0,0,720,1280);
						rcDest.set(0, 0, m_DisplayWidth,m_DisplayHeight);
						
						canvas.drawBitmap(m_bmpBg, rcSrc,rcDest ,null);
						
						m_cMopMgr.AddMop();
						m_cMopMgr.MoveMop();
						
						for(int n=0;n<m_cMopMgr.m_UsedMopCnt;n++){
							if(m_cMopMgr.mop[n].m_Used == true){
								canvas.drawBitmap(m_bmpMop, m_cMopMgr.mop[n].m_x, m_cMopMgr.mop[n].m_y, null);
							}
						}
						sleep(10);
					}
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
				finally{
					if(canvas != null){
						m_SurfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
		
	}
	

	public GameRun(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		m_GameThread = new GameThread(holder,context);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
		m_GameThread.start();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

}
