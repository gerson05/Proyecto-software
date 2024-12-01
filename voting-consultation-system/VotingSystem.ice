#ifndef VOTING_SYSTEM_ICE
#define VOTING_SYSTEM_ICE

module VotingSystem {
    struct VotingTableInfo {
        string documentId;
        string votingLocation;
        int primeFactorsCount;
        bool isPrimeFactorsCountPrime;
        long responseTime;
    }

    interface VotingConsultation {
        VotingTableInfo consultVotingTable(string documentId);
        void registerClient(string clientId);
        void distributeConsultation(string filename);
    }

    interface ClientObserver {
        void notifyConsultationAssignment(string filename, int startLine, int endLine);
        void update(string message);
    }
}

#endif