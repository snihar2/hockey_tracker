import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.query.Query;
import org.hibernate.cfg.Configuration;
import org.hibernate.transform.Transformers;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            configuration.addAnnotatedClass(Match.class);
            configuration.addAnnotatedClass(Quarter.class);

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void main(final String[] args) throws Exception {

        // Example of a distant calculator
        int port = 9876;
        ServerSocket ssock = new ServerSocket(port);

        while (true) { // infinite loop
            System.out.println("Server is listening on port " + port);
            Socket comm = ssock.accept();
            System.out.println(comm);
            System.out.println("connection established");

            new Thread((Runnable) new ServerRunnable(comm)).start();

        }

        }

    /* Method to CREATE an Match in the database */
    public void addMatch(String homeTeam, String awayTeam, int scoreTeamHome, int scoreTeamAway, String date, String location, ArrayList<String> imagePathList,
                         Quarter quarterT, Quarter quarter1, Quarter quarter2, Quarter quarter3, Quarter quarter4){
        System.out.println("adding matche");
        Session session = ourSessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Match match = new Match(homeTeam, awayTeam, scoreTeamHome, scoreTeamAway, date, location, imagePathList);;

            Set<Quarter> subQuarters = new HashSet<>();
            for (int q = 0; q < 5; q++) {
                Quarter quarter;
                if (q == 0) {
                    quarter = quarterT;
                } else if (q == 1) {
                    quarter = quarter1;
                } else if (q == 2) {
                    quarter = quarter2;
                } else if (q == 3) {
                    quarter = quarter3;
                } else {
                    quarter = quarter4;
                }
                quarter.setMatch(match);
                subQuarters.add(quarter);
            }
            match.setSubQuarter(subQuarters);
            session.save(match);
            tx.commit();
            System.out.println("added");
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

//    private Quarter addQuarter(int quarter, int goalsHome, int goalsAway, int shotsHome, int shotsHomeMissed,
//                            int shotsHomeMissedKeeper, int shotsAway, int shotsAwayMissed, int shotsAwayMissedKeeper,
//                            int homeGreenCards, int homeYellowCards, int homeRedCards, int awayGreenCards,
//                            int awayYellowCards, int awayRedCards, int strokeConvertedHome, int strokeNotConvertedHome,
//                            int strokeConvertedAway, int strokeNotConvertedAway, int faultHomeBackstick, int faultHomeKick,
//                            int faultHomeUndercutting, int faultHomeStick, int faultHomeObstruction, int faultAwayBackstick,
//                            int faultAwayKick, int faultAwayUndercutting, int faultAwayStick, int faultAwayObstruction,
//                            int pcConvertedHome, int pcNotConvertedHome, int pcConvertedAway, int pcNotConvertedAway,
//                            int faultPosition25Home, int faultPosition50Home, int faultPosition75Home, int faultPosition100Home,
//                            int faultPosition25Away, int faultPosition50Away, int faultPosition75Away, int faultPosition100Away,
//                            int outsideHomeSide, int outsideHomeClearance, int outsideHomeCorner, int outsideAwaySide,
//                            int outsideAwayClearance, int outsideAwayCorner) {
//
//        final Quarter quarterMatch = new Quarter(quarter, goalsHome, goalsAway, shotsHome, shotsHomeMissed,
//                shotsHomeMissedKeeper, shotsAway, shotsAwayMissed, shotsAwayMissedKeeper,
//                homeGreenCards, homeYellowCards, homeRedCards, awayGreenCards,
//                awayYellowCards, awayRedCards, strokeConvertedHome, strokeNotConvertedHome,
//                strokeConvertedAway, strokeNotConvertedAway, faultHomeBackstick, faultHomeKick,
//                faultHomeUndercutting, faultHomeStick, faultHomeObstruction, faultAwayBackstick,
//                faultAwayKick, faultAwayUndercutting, faultAwayStick, faultAwayObstruction,
//                pcConvertedHome, pcNotConvertedHome, pcConvertedAway, pcNotConvertedAway,
//                faultPosition25Home, faultPosition50Home, faultPosition75Home, faultPosition100Home,
//                faultPosition25Away, faultPosition50Away, faultPosition75Away, faultPosition100Away,
//                outsideHomeSide, outsideHomeClearance, outsideHomeCorner, outsideAwaySide,
//                outsideAwayClearance, outsideAwayCorner);
//        return quarterMatch;
//    }

    public List<Quarter> getMatchQuery(Integer MatchID) {
        Session session = ourSessionFactory.openSession();
        Transaction tx = null;

        List<Quarter> Quarters = new ArrayList<>();

        try {
            tx = session.beginTransaction();
            Quarters = (List<Quarter>) session.createQuery(
                    "FROM Quarter P WHERE P.match.id = " + MatchID).list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return Quarters;
    }

    /* Method to READ ALL the Matchs */
    public List<Match> listMatchs(){
        Session session = ourSessionFactory.openSession();
        Transaction tx = null;

        List<Match> Matchs = new ArrayList<>();

        try {
            tx = session.beginTransaction();
            Matchs = (List<Match>) session.createQuery("FROM Match").list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return Matchs;
    }

//    /* Method to UPDATE salary for an Match */
//    private void updateMatch(Integer MatchID, int price){
//        Session session = factory.openSession();
//        Transaction tx = null;
//
//        try {
//            tx = session.beginTransaction();
//            Match Match = (Match) session.get(Match.class, MatchID);
//            Match.setPrice( price );
//            session.update(Match);
//            tx.commit();
//        } catch (HibernateException e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//    }
//
//    /* Method to DELETE an Match from the records */
//    public void deleteMatch(Integer MatchID){
//        Session session = factory.openSession();
//        Transaction tx = null;
//
//        try {
//            tx = session.beginTransaction();
//            Match Match = (Match)session.get(Match.class, MatchID);
//            session.delete(Match);
//            tx.commit();
//        } catch (HibernateException e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//    }
}