package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {
    private static String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private static int STRING_NUMBER = KEYBOARD.length();

    public static void main(String[] args) {
        /* create guitar strings */
        GuitarString[] strings = new GuitarString[STRING_NUMBER];

        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);
                strings[index].pluck();
            }

            /* compute the superposition of samples */
            double sample  = 0.0;
            for (GuitarString s : strings) {
                sample += s.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString s : strings) {
                s.tic();
            }
        }
    }
}

