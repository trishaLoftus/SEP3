import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class Population {

	public Vector<CandidateSolution> solutions;
	public static final double MATEFACTOR = 0.1;
	public static int N;
	public PreferenceTable pref;

	public Population(int amount, String filename) {
		N = amount / 2;
		solutions = new Vector<CandidateSolution>();
		try {
			pref = new PreferenceTable(filename);
			for (int i = 0; i < amount; i++) {
				CandidateSolution sol = new CandidateSolution(pref);
				solutions.add(sol);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void add(CandidateSolution sol) {
		solutions.add(sol);
	}

	public void order() {
		// order from fittest to weakest
		int j;
		boolean flag = true;
		while (flag) {
			flag = false;
			for (j = 0; j < solutions.size() - 1; j++) {
				if (solutions.get(j).getFitness() < solutions.get(j + 1)
						.getFitness()) {
					CandidateSolution temp = solutions.get(j);
					solutions.set(j, solutions.get(j + 1));
					solutions.set(j + 1, temp);
					flag = true;
				}
			}
		}
	}

	public void cull() {
		order();
		int i = solutions.size();
		while (i > N) {
			solutions.remove(solutions.size() - 1);
			i = solutions.size();
		}
	}

	public void mate() {
		order();
		Random rand = new Random();
		double a = solutions.size() * MATEFACTOR;
		int amount = (int) Math.round(a);
		if (amount < 2) {
			amount = 2;
		}
		int x = 0;
		Vector<CandidateSolution> sols = getFittest(amount);

		for (int i = solutions.size(); i < N * 2; i++) {
			x = rand.nextInt(amount);
			int y = rand.nextInt(amount);
			solutions.add(mate(sols.get(x), sols.get(y)));
		}
	}

	public CandidateSolution mate(CandidateSolution a, CandidateSolution b) {
		CandidateSolution child = new CandidateSolution(pref);
		Random rand = new Random();
		int x = rand.nextInt(2);

		for (int i = 0; i < child.size(); i++) {
			if (x == 0) {
				child.replace(b.getAssignments().get(i));

			} else {
				child.replace(a.getAssignments().get(i));
			}
			x = rand.nextInt(2);
		}
		child.removeClashes();
		return child;
	}

	public Vector<CandidateSolution> getFittest(long amount) {
		order();
		Vector<CandidateSolution> fittest = new Vector<CandidateSolution>();
		for (int i = 0; i < amount; i++) {
			if (solutions.size() > i) {
				fittest.add(solutions.elementAt(i));
			}
		}
		for (CandidateSolution fit : fittest) {
			solutions.remove(fit);
		}
		return fittest;
	}

	public int getBestFitness() {
		return solutions.elementAt(0).getFitness();
	}

	public CandidateSolution getBestSol() {
		return solutions.elementAt(0);
	}
}
