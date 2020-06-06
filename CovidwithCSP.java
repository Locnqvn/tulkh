package planningoptimization115657k62.NguyenQuynhLoc;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class CovidwithCSP {
	int N=10; // so doan khach
	int M=5; // so khu cach ly
	int [] s = {1,2,3,10,4,9,12,14,9,30}; // so luong nguoi trong moi doan khach
	int [] c= {2,30,10,24,29}; // so luong nguoi ma khu cach ly chua toi da
	int [] oneM= {1,1,1,1,1};
	int max_dis=N+M;
	public void solve() {
		Model model = new Model("covid with csp");
		IntVar [][] X = new IntVar[M][N];
		// X[i][j]=1 neu doan khach j duoc dua ve khu i
		// X[i][j]=0 neu doan khach j khong duoc dua ve khu i
		for( int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				X[i][j]=model.intVar("X["+i+"]["+j+"]",0,1);
			}
		}
		// moi doan khach chi duoc dua ve duy nhat 1 noi
		for(int j=0;j<N;j++) {
			IntVar [] y = new IntVar[M];
			for(int i=0;i<M;i++)
				y[i]=X[i][j];
			model.scalar(y, oneM, "=", 1).post();
		}
		
		// so luong khach dua ve moi khu khong duoc lon hon c[i]
		for(int i=0;i<M;i++) {
			model.scalar(X[i], s, "<=", c[i]).post();
		}
		// Ham muc tieu
		IntVar obj = model.intVar("obj",0,max_dis);
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				model.arithm(model.intScaleView(X[i][j], i+N-j), "<=", obj).post();
			}
		}
		model.setObjective(Model.MINIMIZE, obj);
		
		model.getSolver().solve();
		System.out.println("khoang cach min= "+obj.getValue());
		for(int i=0;i<M;i++) {
			System.out.print("cac nhom dua ve khu "+(i+1)+ ": ");
			for(int j=0;j<N;j++) {
				if(X[i][j].getValue()==1) {
					System.out.print((j+1)+" ");
				}
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		CovidwithCSP app= new CovidwithCSP();
		long t= System.currentTimeMillis();
		app.solve();
		System.out.println((System.currentTimeMillis()-t)/1000.0);
	}
}
