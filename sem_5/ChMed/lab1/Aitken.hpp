#include <vector>

class Aitken {
    std::vector<double>x,y;
    int n;

public:
    Aitken(std::vector<double>n_x, std::vector<double>n_y) : x(n_x), y(n_y), n(x.size()) {}

    double getValueInPoint(double find_x) {
        std::vector<double>polynoms = y;

        for (int i = 1; i < n; i++) {
            std::vector<double>next(n - i);
            for (int j = 0; j < n - i; j++) {
                next[j] = (polynoms[j + 1] * (find_x - x[j])
                           - polynoms[j] * (find_x - x[i + j]))
                           / (x[i + j] - x[j]);
            }
            polynoms = next;
        }
        return polynoms[0];
    }

    std::vector<std::vector<double>> buildTable(double findX) {
        int n = x.size();
        std::vector<std::vector<double>> table(n);

        table[0] = y;

        for (int k = 1; k < n; ++k) {
            table[k].resize(n - k);

            for (int j = 0; j < n - k; ++j) {
                table[k][j] =
                    (table[k - 1][j + 1] * (findX - x[j])
                    - table[k - 1][j] * (findX - x[j + k]))
                    / (x[j + k] - x[j]);
            }
        }

        return table;
    }
};