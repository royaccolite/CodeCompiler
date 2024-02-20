#include <iostream>

int sumOfSquares(int n) {
    int ans= (n*(n+1)*((2*n)+1))/6;
    return ans;
}

int main() {
    int n;
    std::cout << "Enter a number: ";
    std::cin >> n;
    std::cout << "Sum of squares: " << sumOfSquares(n) << std::endl;
    return 0;
}
