#include <vector>
#include <iostream>

class Newton {
    std::vector<double>polynom;
    std::vector<double>x,y;
    int n;
    std::vector<std::vector<double>>rr;

public:
    Newton(std::vector<double>n_x, std::vector<double>n_y) : x(n_x), y(n_y), n(x.size()) {
        polynom.resize(n, 0);
        buildRR();
        buildPolynom();
    }

    void buildRR() {
        rr.resize(n);
        for (int i = 0; i < n; i++) rr[0].push_back(y[i]);
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < n - i; j++) {
                rr[i].push_back((rr[i - 1][j + 1] - rr[i - 1][j]) / (x[i + j] - x[j]));
            }
        }
    }

    void buildPolynom() {
        polynom[0] = y[0];
        
        for (int i = 1; i < n; i++) {
            std::vector<double>cur(2);
            cur[0] = -x[0];
            cur[1] = 1;
            for (int j = 1; j < i; j++) {
                std::vector<double>next(cur.size() + 1, 0);
                next[0] = -1 * cur[0] * x[j];
                for (int k = 1; k < next.size() - 1; k++) {
                    next[k] = cur[k - 1] - x[j] * cur[k];
                }
                next[next.size() - 1] = cur[cur.size() - 1];
                cur = next;
            }

            for (int j = 0; j < cur.size(); j++) {
                polynom[j] += cur[j] * rr[i][0];
            }
        }
    }

    std::vector<double> getPolynom() {
        return polynom;
    }

    double getValueInPoint(double find_x) {
        double value = 0;
        for (int i = n - 1; i >= 0; i--) {
            value = value * find_x + polynom[i];
        }
        return value;
    }

    std::vector<std::vector<double>> getRR() {
        return rr;
    }
};