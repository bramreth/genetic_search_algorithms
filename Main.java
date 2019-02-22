import java.util.ArrayList;

public class Main {

    static double[] sol1 = {0,0};
    static boolean[] sol2 = new boolean[100];

    static double lower_bound = -500;
    static double higher_bound = 500;

    static double timeout = 14500; //ms, 10 s 10000

    public static long start = 0;

    /**
     * convert the input into a value rounded between the higher and lower bounds declared above
     * @param in
     * @return
     */
    public static double bound(double in){
        if (in > higher_bound) {
            return higher_bound;
        }
        if (in < lower_bound) {
            return lower_bound;
        }
        return in;

    }

    /**
     * this function performs the greedy search for assessment 1 with a slight twist, the longer is spent on a result
     * without nearby improvements being found, the magnitude of the tweaks to the x and y values increase. as such we
     * can explore a larger area if we get stuck on a local peak
     * @return
     */
    public static double[] greedy(){
        double[] cur = sol1;
        double[] ans = new double[2];
        double mutation_a, mutation_b;
        double[] candidate = cur;
        double fit =Assess.getTest1(cur[0],cur[1]);
        //this mutation rate allows us to change the search space of our algorithm
        double mutation_rate = 1;
        //search until half of the alloted time is used.
        while ((System.currentTimeMillis() - start) < timeout){
            //this creates a change that is proportioanl to the mutation rate multiplied by a value between 0.5 and -0.5
            mutation_a = (Math.random()-0.5) * mutation_rate;
            mutation_b = (Math.random()-0.5) * mutation_rate;

            //add the tweaks to our candidate answer
            candidate[0] += mutation_a;
            candidate[1] += mutation_b;
            //restrict our answer to the limits of the input field (-500 : 500)
            candidate[0] = bound(candidate[0]);
            candidate[1] = bound(candidate[1]);
            //assess our candidate solution for its improvement over the previous best answer
            double result = Assess.getTest1(candidate[0], candidate[1]);

            if (result < fit){
                //System.out.println(candidate[0] + " " + candidate[1] + " : " + result);
                fit = result;
                //save our results to an ans array
                ans[0] = candidate[0];
                ans[1] = candidate[1];
                cur[0] = candidate[0];
                cur[1] = candidate[1];
                mutation_rate = 1;
            }else{
                candidate[0] = cur[0];
                candidate[1] = cur[1];
                //on a failed attempt increment our mutation rate
                mutation_rate += 0.001;
                //System.out.println(mutation_rate);
                //this cap prevents our mutation rate from becoming too ridiculous however is higher than i would like
                mutation_rate = bound(mutation_rate);
            }
        }
        //--------System.out.println("ans " + ans[0] + " " + ans[1]);
        return ans;

    }

    public static double candidate_solution_1(){

        //run the greedy search and assign its candidate to our solution
        sol1 = greedy();

        double fit =Assess.getTest1(sol1[0],sol1[1]);

        return fit;
    }

    /**
     * mutates a random bit from a list
     * @param lst
     * @return
     */
    public static boolean[] mutate(boolean[] lst){
        int index = (int)(Math.random() * 100);
        lst[index] = !lst[index];
        return lst;
    }

    /**
     * breeds two parent lists to create a child and then mutate a random bit twice.
     * the first half of array 1 is spliced onto the second half of array 2
     * @param a
     * @param b
     * @return
     */
    public static boolean[] breed(boolean[] a, boolean[] b){
        boolean[] result = new boolean[100];
        for(int x = 0; x < 100; x++){
            if(x < 50){
                result[x] = a[x];
            }else{
                result[x] = b[x];
            }
        }
        //two mutations are used as when we have a good solution it becomes harder to find better solutions without
        //worsening our solution first, as such this gives us a better chance of finding a lateral solution
        result = mutate(result);
        result = mutate(result);

        return result;
    }

    public static void print_details(boolean[] list){
        double[] tmp =(Assess.getTest2(list));
        System.out.println("weight : " + tmp[0] + " and utility: " + tmp[1]);
        System.out.println("result: " + assess_solution(list));
    }

    /**
     * fitness evaluation for problem two. in order to make the algorithm avoid exceeding the maximum weight,
     * the weight is subtracted from the fitness score if the weight is exceeded.
     * @param solution
     * @return
     */
    public static double assess_solution(boolean[] solution){
        double max = 500;
        double[] tmp =(Assess.getTest2(solution));
        if(tmp[0] > 500){
            //compensate utility for overburdening
            tmp[1] -= tmp[0];
        }
        return tmp[1];
    }

    /**
     * the genetic solution for assessment two
     * @return
     */
    public static boolean[] genetic(){

        //a list to store our best children in
        ArrayList<boolean[]> results = new ArrayList<>();
        boolean[] cur = sol2;

        //we need to create our first parents to breed children from
        boolean[] parent_a = new boolean[100];
        boolean[] parent_b = new boolean[100];
        //as a cheap heuristic a make one parent all true and the other all false, they can be randomised by
        //the math.random lines, little noticeable difference in preformance from either approach
        for(int i=0;i< parent_a.length; i++){
            parent_a[i]= (Math.random()>0.5);
            //parent_a[i]=true;
        }
        for(int i=0;i< parent_b.length; i++){
            parent_b[i]= (Math.random()>0.5);
            //parent_a[i]=false;
        }

        /*
        from these two parents two children are created by splicing the parents both left to right and right to left
        (see the breed function)
         */
        boolean[] child_a = breed(parent_a, parent_b);
        boolean[] child_b = breed(parent_b, parent_a);

        //add the original parents and the new children to our list of solutions, this is enough to kick start the
        //genetic search
        results.add(parent_a);
        results.add(parent_b);
        results.add(child_a);
        results.add(child_b);

        double utility = -Double.MAX_VALUE;
        start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < timeout) {
            double first = -Double.MAX_VALUE;
            //create 3 blank results for assessment
            boolean[] best = new boolean[100];
            boolean[] second_choice = new boolean[100];
            //--------System.out.println("results:");

            /*
            for each of the results from the last excecution find the result that had the best utility and pass it onto
            the children of the next batch.
             */
            for (boolean[] item : results) {
                double res = assess_solution(item);
                //----------System.out.println(res);
                if (res > first) {
                    best = item;
                    first= res;
                }
            }

            /*
            the other solution is selected randomly from the sample set, this gives us a good chance for exploration
             across the problem space as poor solutions can be carried forwards.
             */
            second_choice = results.get((int)(Math.random() * results.size()));

            //empty the list of results, add the best child, a random choice and the results of breeding those two.
            results.clear();
            results.add(best);
            results.add(second_choice);
            results.add(breed(best, second_choice));
            results.add(breed(second_choice, best));
            //update our best utility and how we got it
            utility = first;
            cur = best;

        }

        return cur;
    }

    public static double[] candidate_solution_2(){
        for(int i=0;i< sol2.length; i++){
            sol2[i]= (Math.random()>0.5);
        }
        //store the result of our candidate genetic solution
        sol2 = genetic();
        double[] tmp =(Assess.getTest2(sol2));
        return tmp;
    }

    public static void main(String[] args)
    {
        init();
    }
    /**
     * this is the setup that generates all the answers. init must be run.
     */
    public static void init(){
        long startT=System.currentTimeMillis();

        start = startT;
        double fit = candidate_solution_1();
        double[] tmp = candidate_solution_2();

        //Assess.checkIn(name,login,sol1,sol2);
        //Do not delete or alter the next 2 lines
        //Changing them will lead to loss of marks
        long endT= System.currentTimeMillis();
        //System.out.println("Total execution time was: " +  ((endT - startT)/1000.0) + " seconds");
    }

    public static double[] get_sol_1(){
        return sol1;
    }

    public static boolean[] get_sol_2(){
        return sol2;
    }
}
