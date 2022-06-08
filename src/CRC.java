import java.util.Random;
import java.util.Scanner;

/**
 * Αλγόριθμος ανίχνευσης λαθών CRC
 * σε γλώσσα προγραμματισμού Java.
 *
 * @author Ολγκέρ Χότζα (3899)
 * @version 1.0
 * @email ochotzas@csd.auth.gr
 * @date Μάιος 2022
 */
public class CRC
{
    private String FCS;
    private String P;
    private long N;
    private int K;

    private double BER;
    private long foundErrors;
    private long notFoundErrors;

    /**
     * Κατασκευαστής της κλάσης CRC για αρχικοποίηση των μεταβλητών
     */
    public CRC()
    {
        FCS = "";
        P = "";
        N = 0L;
        BER = 0.0;
        K = 0;
        foundErrors = 0L;
        notFoundErrors = 0L;
    }

    /**
     * Πραγματοποιείται ο αλγόριθμος της δυαδικής
     * διαίρεσης μεταξύ του T και του P.
     *
     * @param T Ακολουθία των n bits.
     * @param P Προκαθορισμένος αριθμός των n-k+1 bits.
     * @return Το αποτέλεσμα της διαίρεσης.
     */
    private String Div(String T, String P)
    {
        int pointerOfT = P.length();
        this.FCS = T.substring(0, P.length());

        while (true)
        {
            // Κάνουμε XOR το FCS με το P
            this.FCS = XOR(this.FCS, P);

            try
            {
                // Αφαιρούμε όλα τα μηδενικά
                this.FCS = this.FCS.substring(this.FCS.indexOf("1"));

            } catch (Exception ignored)
            {
                // Αν δεν υπάρχει κανένα μηδενικό, τότε "αδειάζουμε" την FCS
                this.FCS = "";
            }

            try
            {
                // Αντιγράφοντας bits από το T στον FCS έως ότου το P και το FCS να έχουν ίδιο μήκος bits
                while (this.FCS.length() < P.length())
                    this.FCS = this.FCS.concat(Character.toString(T.charAt(pointerOfT++)));

            } catch (Exception ignored)
            {
                // Τερματίζουμε τη διαδικασία όταν δεν υπάρχουν άλλα bits στο T για να αντιγράψουμε
                break;
            }
        }

        // Συμπληρώνουμε μηδενικά bits στην περίπτωση που
        // χρειάζεται bits έπειτα από το τελευταίο bit του T
        while (this.FCS.length() < P.length() - 1)
            this.FCS = this.FCS.concat("0");

        return this.FCS;
    }

    /**
     * Πραγματοποιείται ο αλγόριθμος της διαίρεσης modulo-2
     * (XOR) σε δυαδικά ψηφία μεταξύ του FCS και του P.
     *
     * @param FCS Ακολουθία ελέγχου σφάλματος F των n-k bits.
     * @param P   Προκαθορισμένος αριθμός των n-k+1 bits.
     * @return Το αποτέλεσμα της modulo-2 διαίρεσης.
     */
    private String XOR(String FCS, String P)
    {
        StringBuilder finalFCS = new StringBuilder();
        int eachBit = 0;

        while (eachBit < FCS.length())
        {
            if (FCS.charAt(eachBit) == P.charAt(eachBit))
                finalFCS.append("0");
            else
                finalFCS.append("1");
            eachBit++;
        }

        return finalFCS.toString();
    }

    /**
     * Δημιουργεί μια τυχαία παραγόμενη ακολουθία bits
     * τύπου συμβολοσειρά με μήκος k bits.
     * <p>
     * Η παραγόμενη ακολουθία είναι ισοπίθανη σε πλήθος
     * άσσων και μηδενικών.
     *
     * @param K Το μήκος της ακολουθίας bits.
     * @return Η τυχαία ισοπίθανη παραγόμενη ακολουθία bits.
     */
    private String RandomBitGenerator(int K)
    {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();

        for (int i = 0; i < K; i++)
            sb.append(r.nextInt(2));

        return sb.toString();
    }

    private void GetInput()
    {
        try
        {
            Scanner in = new Scanner(System.in);

            System.out.println(" (>) K: ");
            K = Integer.parseInt(in.nextLine());

            while (true)
            {
                System.out.println(" (>) P: ");
                P = in.nextLine();

                // Ελέγχουμε αν το P ξεκινά από 1 και τελειώνει σε 1 και έχει μόνο 0 ή 1
                if (P.charAt(0) == '1' && P.charAt(P.length() - 1) == '1' /*&& P.length() >= N-K*/ && P.matches("[01]+"))
                    break;

                // Εμφανίζουμε το αντίστοιχο μήνυμα πιθανού σφάλματος
                System.out.println(" (!) Enter P containing only 0 and 1" +
                        " and starting and ending with 1 and greater than" +
                        " or equal to K");
            }

            System.out.println(" (>) BER: ");
            BER = Double.parseDouble(in.nextLine());

            System.out.println(" (>) N: ");
            N = Long.parseLong(in.nextLine());
        } catch (Exception e)
        {
            System.out.println(" (!) Not valid input/s\n (!) Error: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    /**
     * Εκτυπώνει τα αποτελέσματα
     */
    public void Print()
    {
        System.out.println("--------------------------");
        System.out.printf("| Total errors \t\t\t | \t %d \n", foundErrors);
        System.out.printf("| Undetected errors \t | \t %d (%.3f) \n", notFoundErrors, ((double) notFoundErrors / foundErrors) * 100);
        System.out.printf("| Detected errors \t\t | \t %d (%.3f) \n", foundErrors - notFoundErrors, ((double) (foundErrors - notFoundErrors) / foundErrors) * 100);
        System.out.println("--------------------------");
    }

    /**
     * Εκτελεί τον κώδικα του CRC
     */
    public void Run()
    {
        // Παίρνουμε τιμές από τον χρήστη
        GetInput();

        if (K >= 1000 && N >= 10000)
            System.out.println(" (*) This may take a while...");

        long loop = 0;
        while (loop++ < N)
        {
            String randomBits = RandomBitGenerator(K);
            String FCS = Div(randomBits.concat("0".repeat(P.length() - 1)), P);

            String received = randomBits.concat(FCS);
            StringBuilder transData = new StringBuilder();

            for (int i = 0; i < received.length(); i++)
            {
                // Αν είναι 0 ή 1, τότε προσθέτουμε το bit στο transData
                if (new Random().nextDouble() > BER)
                {
                    transData.append(received.charAt(i));
                    continue;
                }

                // Διαδικασία ισότιμης πιθανότητας ελέγχου για το περιστατικό σφάλμα
                // όπου αντιστρέφει τα bits που έχουν παραλάβει και τα επιστρέφει
                transData.append(received.charAt(i) == '0' ? "1" : "0");
            }

            if (!received.equals(transData.toString()))
            {
                // Σφάλματα
                foundErrors++;

                // Μη εντοπισμένα σφάλματα
                if (Div(transData.toString(), P).equals("0".repeat(P.length() - 1)))
                    notFoundErrors++;
            }
        }
    }

    /**
     * Εκτέλεση του προγράμματος
     */
    public static void main(String[] args)
    {
        do
        {
            CRC crc = new CRC();
            crc.Run();
            crc.Print();

            System.out.println(" (>) Press any key to run again or 'q' to quit");
        } while (!new Scanner(System.in).nextLine().equals("q"));
    }
}