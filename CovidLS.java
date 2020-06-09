package planningoptimization115657k62.NguyenQuynhLoc;


import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;


public class CovidLS {
	int N=10; // so doan khach
	int M=5; // so khu cach ly
	int [] s = {1,2,3,10,4,9,12,14,9,30}; // so luong nguoi trong moi doan khach
	int [] c= {2,30,10,24,29}; // so luong nguoi ma khu cach ly chua toi da
	Random Rd= new Random();
	LocalSearchManager mgr= new LocalSearchManager();
	VarIntLS [] X= new VarIntLS[N];;
	ConstraintSystem S=new ConstraintSystem(mgr);;
	IFunction [] V=new IFunction[M];;
	class Move{
		int i;
		int v;
		public Move(int x,int y){
			this.i=x;
			this.v=y;
		}
	}
	public void stateModel() {
		for(int i=0;i<N;i++)
			X[i]=new VarIntLS(mgr, 0,M-1);
		// constraint
		V=new IFunction[M];
		for(int j=0;j<M;j++) {
			V[j]=new ConditionalSum(X, s, j);
			S.post(new LessOrEqual(V[j], c[j]));
		}
		mgr.close();
	}
	public void getresult() {
		int dis_max=Integer.MIN_VALUE;
		for(int i=0;i<N;i++)
			dis_max=Math.max(dis_max, X[i].getValue()+N-i);
		System.out.println("dis max =" + dis_max);
		for(int i=0;i<M;i++) {
			System.out.print("khu "+(i+1)+": ");
			for(int j=0;j<N;j++) {
				if(X[j].getValue()==i) {
					System.out.print((j+1)+" ");
				}
			}
			System.out.println();
		}
	}
	
	public int getAssignDelta(int pos, int val) {
		int deltanew=Integer.MIN_VALUE;
		int deltaold=Integer.MIN_VALUE;
		for(int i=0;i<N;i++) {
			deltaold=Math.max(deltaold, X[i].getValue()+N-i);
		}
		for(int i=0;i<N;i++) {
			if(i==pos) {
				deltanew=Math.max(deltanew, val+N-pos);
			}else {
				deltanew=Math.max(deltanew, X[i].getValue()+N-i);
			}
		}
		return deltanew-deltaold;
	}
	
	public void Search1(VarIntLS[] X,ConstraintSystem S,int maxInter,int tbl,int maxStable) {
		int [][] tabu= new int[N][M];
		for(int i=0;i<N;i++)
			for(int j=0;j<M;j++)
				tabu[i][j]=-1;
		int nic=0;
		ArrayList<Move> cand= new ArrayList<>();
		int it=0;
		int best=S.violations();
		while(it<maxInter&&S.violations()>0) {
			int mindelta=Integer.MAX_VALUE;
			for(int i=0;i<N;i++) {
				for(int v=X[i].getMinValue();v<X[i].getMaxValue();v++) {
					if(X[i].getValue()!=v) {
						int delta=S.getAssignDelta(X[i], v);
						if(tabu[i][v]<=it||delta+S.violations()<best) {
							if(delta<mindelta) {
								mindelta=delta;
								cand.clear();
								cand.add(new Move(i,v));
							}else if(delta==mindelta){
								cand.add(new Move(i,v));
							}
						}
					}
				}
			}
			Move m = cand.get(Rd.nextInt(cand.size()));
			X[m.i].setValuePropagate(m.v);
			tabu[m.i][m.v]=it+tbl;
			if(S.violations()<best) {
				best=S.violations();
				nic=0;
			}else {
				nic++;
				if(nic>=maxStable) { //restart
					for(int i = 0; i < N; i++) {
						X[i].setValuePropagate(Rd.nextInt(M));
					}
					if(S.violations()<best)
						best=S.violations();
				}
			}
			//System.out.println("Step " + it + " violations = " + S.violations());
			it++;
		}
	}
	public boolean checkstop(VarIntLS [] X) {
		for(int i=0;i<M;i++) {
			if(X[i].getValue()!=i)
				return false;
		}
		return true;
	}
	public void Search2(VarIntLS[] X,ConstraintSystem S,int maxInter) {
		ArrayList<Move> cand= new ArrayList<>();
		int it=0;
		while(it<maxInter&&checkstop(X)!=true) {
			int mindeltaF=Integer.MAX_VALUE;
			for(int i=0;i<N;i++) {
				for(int v=X[i].getMinValue();v<X[i].getMaxValue();v++) {
					if(X[i].getValue()!=v) {
						int deltaS=S.getAssignDelta(X[i], v);
						if(deltaS>0)
							continue;
						int deltaF=getAssignDelta(i, v);
						if(deltaF<=0) {
							if(deltaF<mindeltaF) {
								mindeltaF=deltaF;
								cand.clear();
								cand.add(new Move(i,v));
							}else if(deltaF==mindeltaF){
								cand.add(new Move(i,v));
							}
						}
					}
				}
			}
			if(cand.size()==0)
				break;
			int pos_new=Rd.nextInt(cand.size());
			Move m = cand.get(pos_new);
			cand.remove(pos_new);
			X[m.i].setValuePropagate(m.v);
			
			System.out.println("Step " + it + " mindelta = " + mindeltaF);
			it++;
		}
	}
	public void solve() {
		stateModel();
		boolean checksearch1=false;
		for(int i=0;i<1000;i++) {
			Search1(X, S, 10000, 3, 100);
			if(S.violations()==0) {
				System.out.println("complete Search 1");
				checksearch1=true;
				break;
			}				
		}
		if(!checksearch1) {
			System.out.println("no solution");
			return;
		}
		Search2(X, S, 1000);
	}
	public static void main(String[] args) {
		CovidLS app= new CovidLS();
		long t=System.currentTimeMillis();
		app.solve();
		app.getresult();
		System.out.println("time = "+(System.currentTimeMillis()-t)/1000.0);
	}
}
