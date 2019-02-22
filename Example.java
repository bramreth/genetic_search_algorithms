import java.lang.Math;
// You may uncomment the following line if you wish.
// import java.util.*; 


class Example {
		public static void main(String[] args){
				//Do *not* delete/alter the next line
				//Changing the next line will lead to loss of marks
		    long startT=System.currentTimeMillis();

		    //Edit this according to your name and login
			String name="Bram Williams";
			String login = "bw308";

			Main answer = new Main();
			answer.init();

			double[] sol1= answer.get_sol_1();

			boolean[] sol2 = answer.get_sol_2();

			Assess.checkIn(name,login,sol1,sol2);
      
			//Do not delete or alter the next 2 lines
			 //Changing them will lead to loss of marks
		        long endT= System.currentTimeMillis();
			System.out.println("Total execution time was: " +  ((endT - startT)/1000.0) + " seconds");

	  }



}
