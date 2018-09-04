import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

class Reviewer{
    double utility;
    double prob_Rt_cond_S;
    double prob_Rt_cond_F;

    double prob_Rf_cond_S;
    double prob_Rf_cond_F;
    double prob_Rt;
    double prob_Rf;
    double prob_S_cond_Rt;
    double prob_F_cond_Rt;

    Reviewer(double utility, double prob_Rt_cond_S, double prob_Rt_cond_F){
        this.utility = utility;
        this.prob_Rt_cond_S = prob_Rt_cond_S;
        this.prob_Rt_cond_F = prob_Rt_cond_F;

        this.prob_Rf_cond_S = 1-prob_Rt_cond_S;
        this.prob_Rf_cond_F = 1-prob_Rt_cond_F;
        this.prob_Rt = this.prob_Rt_cond_S*Main.PROB_SUCCESS + this.prob_Rt_cond_F*Main.PROB_FAILURE;
        this.prob_Rf = this.prob_Rf_cond_S*Main.PROB_SUCCESS + this.prob_Rf_cond_F*Main.PROB_FAILURE;
        this.prob_S_cond_Rt = this.prob_Rt_cond_S*Main.PROB_SUCCESS/this.prob_Rt;
        this.prob_F_cond_Rt = this.prob_Rt_cond_F*Main.PROB_FAILURE/this.prob_Rt;

    }
    void print_input_inform(){
        System.out.printf("Utility=%.0f P(R=T|S)=%.2f P(R=T|F)=%.2f\n",utility, prob_Rt_cond_S, prob_Rt_cond_F);
    }
    void print_all_inform(){
        System.out.printf("Utility=%.0f P(R=T|S)=%.2f P(R=T|F)=%.2f P(R=T)=%.2f P(R=F)=%.2f P(S|R=T)=%.2f P(F|R=T)=%.2f\n",utility, prob_Rt_cond_S, prob_Rt_cond_F, prob_Rt, prob_Rf, prob_S_cond_Rt, prob_F_cond_Rt);
    }
}

public class Main {
    static int NUM_R;
    static double UTILITY_SUCCESS;
    static double UTILITY_FAILURE;
    static double PROB_SUCCESS;
    static double PROB_FAILURE;
    static Reviewer REVIEWERS[];
    static HashSet<Integer> CHANCE_SET = new HashSet<>();   // set of numbers represent choices

    public static void main(String[] args) throws IOException {
        if(args.length!=1){

        }else{
            get_information_from(args[0]);
            print_input_information();
            Publisher_Reviewer_Algo();
        }
    }

    static void Publisher_Reviewer_Algo(){
        initialize_chance_set();

        Reviewer start = new Reviewer(0,1,1);
        Reviewer prevR = start;
        boolean review = true; // initial review

        double exp_index_pair[] = dfs(CHANCE_SET,start,review);

        double EXP = exp_index_pair[0];
        int next_move = (int)exp_index_pair[1];
        System.out.println("Expected value: "+EXP);
        System.out.print(convert_num_to_choice(next_move));
        while(true) {
            CHANCE_SET.remove(next_move);   // remove current choice from chance set
            Scanner scanner = new Scanner(System.in);
            String review_line = scanner.nextLine();    // get the review from current reviewer
            double exp_index_p[];
            Reviewer currR;     // condition reviewer
            if(review == true){
                currR = new Reviewer(prevR.utility + REVIEWERS[next_move - 1].utility,
                        REVIEWERS[next_move - 1].prob_Rt_cond_S * prevR.prob_Rt_cond_S / prevR.prob_Rt,
                        REVIEWERS[next_move - 1].prob_Rt_cond_F * prevR.prob_Rt_cond_F / prevR.prob_Rt);
            }else{
                currR = new Reviewer(prevR.utility + REVIEWERS[next_move - 1].utility,
                        REVIEWERS[next_move - 1].prob_Rt_cond_S * prevR.prob_Rf_cond_S / prevR.prob_Rf,
                        REVIEWERS[next_move - 1].prob_Rt_cond_F * prevR.prob_Rf_cond_F / prevR.prob_Rf);

            }
            if (review_line.equalsIgnoreCase("yes")) {
                review = true;
                CHANCE_SET.remove(-1);
                exp_index_p = dfs(CHANCE_SET, currR, true);
                prevR = currR;
                CHANCE_SET.add(-1);
            } else {
                review = false;
                CHANCE_SET.remove(0);
                exp_index_p = dfs(CHANCE_SET, currR, false);
                prevR = currR;
                CHANCE_SET.add(0);
            }
            //System.out.println("Expected value: "+exp_index_p[0]);
            System.out.print(convert_num_to_choice((int)exp_index_p[1]));
            next_move = (int)exp_index_p[1];
            if(next_move<1){
                break;
            }
        }
    }

    static String convert_num_to_choice(int i){
        if( i == 0){
            return "Publish\n";
        }
        if(i == -1){
            return "Reject\n";
        }
        else{
            return "Consult Reviewer "+i+": ";
        }
    }

    static void initialize_chance_set(){
        CHANCE_SET.add(-1);     // -1 represent reject
        CHANCE_SET.add(0);      // 0 represent publish
        for(int i = 0; i<NUM_R; i++){   // others represent reviewers
            CHANCE_SET.add(i+1);
        }
    }

    static double[] dfs(HashSet<Integer> chance_set, Reviewer prevR, boolean review){
        double max_val = -999;
        int index = 0;
        for(int i : chance_set){
            double exp = 0.0;
            if(i == 0){ // Publish
                exp = prevR.prob_S_cond_Rt*(UTILITY_SUCCESS-prevR.utility)+prevR.prob_F_cond_Rt*(UTILITY_FAILURE-prevR.utility);
                //System.out.println(exp);
            }else if (i == -1){ // Reject
                exp = - prevR.utility;
                //System.out.println(exp);
            }else{  // Reviewer i
                chance_set.add(0);  // after this reviewer's review, the writer has the chance to publish
                chance_set.add(-1); // after this reviewer's review, the writer has the chance to publish
                Reviewer currR;
                if(review == true) {
                    currR = new Reviewer(prevR.utility + REVIEWERS[i - 1].utility,
                            REVIEWERS[i - 1].prob_Rt_cond_S * prevR.prob_Rt_cond_S/prevR.prob_Rt,
                            REVIEWERS[i - 1].prob_Rt_cond_F * prevR.prob_Rt_cond_F/prevR.prob_Rt);
                }else{
                    currR = new Reviewer(prevR.utility + REVIEWERS[i - 1].utility,
                            REVIEWERS[i - 1].prob_Rt_cond_S * prevR.prob_Rf_cond_S/prevR.prob_Rf,
                            REVIEWERS[i - 1].prob_Rt_cond_F * prevR.prob_Rf_cond_F/prevR.prob_Rf);
                }
                HashSet<Integer> chance_set_y = new HashSet<>();
                HashSet<Integer> chance_set_n = new HashSet<>();
                for(int c : chance_set){
                    chance_set_y.add(c);
                    chance_set_n.add(c);
                }
                chance_set_y.remove(-1);    // pruning the chance to reject, because reviewer say yes
                chance_set_y.remove(i);
                chance_set_n.remove(0);     // pruning the chance to publish, because reviewer say no
                chance_set_n.remove(i);
                double[] exp_y = dfs(chance_set_y, currR, true);
                double[] exp_n = dfs(chance_set_n, currR, false);
                exp = currR.prob_Rt*exp_y[0]+currR.prob_Rf*exp_n[0];
                //System.out.println(exp+ "="+ currR.prob_Rt+"*"+exp_y[0]+"+"+currR.prob_Rf+"*"+exp_n[0]);
            }
            if(exp >max_val){
                max_val = exp;
                index = i;
            }
        }
        //System.out.println(index);
        double exp_index_pair[] = new double[2];
        exp_index_pair[0] = max_val;
        exp_index_pair[1] = index;
        return exp_index_pair;
    }

    static void get_information_from(String filename) throws IOException {
        File file = new File(filename);
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String line;
        line=bf.readLine();
        String temp[] = line.split(" ");
        NUM_R = Integer.parseInt(temp[0]);
        UTILITY_SUCCESS = Double.parseDouble(temp[1]);
        UTILITY_FAILURE = Double.parseDouble(temp[2]);
        PROB_SUCCESS = Double.parseDouble(temp[3]);
        PROB_FAILURE = 1-PROB_SUCCESS;
        REVIEWERS = new Reviewer[NUM_R];
        for(int i=0; i<NUM_R; i++){
            line=bf.readLine();
            String t[] = line.split(" ");
            Reviewer r = new Reviewer(Double.parseDouble(t[0]),Double.parseDouble(t[1]),Double.parseDouble(t[2]));
            REVIEWERS[i]=(r);
        }
    }

    static void print_input_information(){
        System.out.printf("Utility of Success: %.2f, Utility of Failure: %.2f, P(S)=%.2f\n",UTILITY_SUCCESS, UTILITY_FAILURE, PROB_SUCCESS);
        int index = 1;
        for (Reviewer r : REVIEWERS){
            System.out.print("Reviewer "+index+": ");
            r.print_input_inform();
            index++;
        }
        System.out.println();
    }
}
