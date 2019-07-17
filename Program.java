import java.util.ArrayList;
import java.util.Random;

public class Program
{

	final static int NUM_PROCS = 6; // How many concurrent processes
	final static int TOTAL_RESOURCES = 30; // Total resources in the system
	final static int MAX_PROC_RESOURCES = 13; // Highest amount of resources any process could need
	final static int ITERATIONS = 30; // How long to run the program
	static Random rand = new Random();
	
	public static void main(String[] args)
	{
		int totalHeldResources = 0;
		// The list of processes:
		ArrayList<Proc> processes = new ArrayList<Proc>();
		for (int i = 0; i < NUM_PROCS; i++)
			processes.add(new Proc(MAX_PROC_RESOURCES - rand.nextInt(3))); // Initialize to a new Proc, with some small range for its max
		
		// Run the simulation:
		for (int i = 0; i < ITERATIONS; i++)
		{
			// loop through the processes and for each one get its request
			for (int j = 0; j < processes.size(); j++)
			{
				// Get the request
				int currRequest = processes.get(j).resourceRequest(TOTAL_RESOURCES - totalHeldResources);

				// just ignore processes that don't ask for resources
				if (currRequest == 0)
					continue;
				if(currRequest < 0) {
					System.out.println("Process " + j + " finished, returned " + Math.abs(currRequest));
					continue;
				}
				int temptTotalHeldResources = totalHeldResources + currRequest;
				int available = TOTAL_RESOURCES - temptTotalHeldResources;
				ArrayList<Integer[]> list = new ArrayList<Integer[]>();
				for(Proc p : processes) {
					if(p.equals(processes.get(j))) 
						list.add(new Integer[] { p.getHeldResources() + currRequest, p.getMaxResources() });
					else 
						list.add(new Integer[] { p.getHeldResources(), p.getMaxResources() });
				}
				boolean found;
				boolean safe = true;
				while(!list.isEmpty()) {
					found = false;
					for(int x = 0; x < list.size(); x++) {
						if(list.get(x)[1] - list.get(x)[0] <= available) {
							available += list.get(x)[0];
							list.remove(x);
							found = true;
						}
					}
					if(!found) {
						safe = false;
						break;
					}					
				}
				if(safe) {
					//Grant resources
					processes.get(j).addResources(currRequest);
					totalHeldResources += currRequest;
					System.out.println("Process " + j + " requested " + currRequest + ", granted.");
				}
				else {
					System.out.println("Process " + j + " requested " + currRequest + ", denied.");
				}

				// At the end of each iteration, give a summary of the current status:
				System.out.println("\n***** STATUS *****");
				System.out.println("Total Available: " + (TOTAL_RESOURCES - totalHeldResources));
				for (int k = 0; k < processes.size(); k++)
					System.out.println("Process " + k + " holds: " + processes.get(k).getHeldResources() + ", max: " +
							processes.get(k).getMaxResources() + ", claim: " + 
							(processes.get(k).getMaxResources() - processes.get(k).getHeldResources()));
				System.out.println("***** STATUS *****\n");
				
			}
		}

	}

}
