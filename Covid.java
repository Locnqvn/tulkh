package planningoptimization115657k62.NguyenQuynhLoc;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class Covid {
	static {
		System.loadLibrary("jniortools");
	}
	int N=4; // so doan khach
	int M=3; // so khu cach ly
	int [] s = {1,2,3,10}; // so luong nguoi trong moi doan khach
	int [] c= {3,10,3}; // so luong nguoi ma khu cach ly chua toi da
	public void solve() {
		MPSolver solver = new MPSolver("Covid",MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		MPVariable [][] X = new MPVariable[M][N];
		MPVariable [] C= new MPVariable[M];
		int dis_max=N+M;
		// X[i][j]=0|1
		for( int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				X[i][j]=solver.makeIntVar(0, 1, "X["+i+"]["+j+"]");
			}
		}
		
		// moi doan khach chi duoc dua ve 1 noi cach ly
		for( int j=0;j<N;j++) {
			MPConstraint c= solver.makeConstraint(1,1);
			for(int i=0;i<M;i++) {
				c.setCoefficient(X[i][j], 1);
			}
		}
		
		// so luong khach o khu i phai nho hon c[i]
		for(int i=0;i<M;i++) {
			int x=c[i];
			MPConstraint c= solver.makeConstraint(0,x);
			for(int j=0;j<N;j++) {
				c.setCoefficient(X[i][j], s[j]);
			}
		}
		
		// Ham muc tieu
		MPVariable y= solver.makeIntVar(0, dis_max, "y");
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				MPConstraint c= solver.makeConstraint(0,dis_max);
				c.setCoefficient(X[i][j], j-i-N);
				c.setCoefficient(y, 1);
			}
		}
		MPObjective obj= solver.objective();
		obj.setCoefficient(y, 1);
		obj.minimization();
		ResultStatus rs= solver.solve();
		if(rs!=ResultStatus.OPTIMAL) {
			System.out.println("no solution");
		}else {
			System.out.println("khoang cach min= "+ obj.value());
			for(int i=0;i<M;i++) {
				System.out.print("cac nhom dua ve khu "+(i+1)+": ");
				for(int j=0;j<N;j++) {
					if(X[i][j].solutionValue()==1) {
						System.out.print((j+1)+" ");
					}
				}
				System.out.println();
			}
		}
	}
	public static void main(String[] args) {
		Covid app= new Covid();
		app.solve();
	}
}
