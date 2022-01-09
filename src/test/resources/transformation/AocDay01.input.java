class Aoc {
    public static void main(String[] args) throws Exception {
        InputHelper inputHelper = new InputHelper();
        OutputHelper outputHelper = new OutputHelper();
        Day01 day01 = new Day01();

        IntList numbers = inputHelper.readIntegers();
        outputHelper.printDay(1);
        outputHelper.printSolution(day01.partOne(numbers));
        outputHelper.printSolution(day01.partTwo(numbers));
    }
}

class Day01 {
    public int partOne(IntList numbers) {
        int sum = 0;
        int i = 0;
        while (i < numbers.size - 1) {
            int first = numbers.data[i];
            int next = numbers.data[i + 1];

            if (next > first) {
                sum = sum + 1;
            }
            i = i +1;
        }
        return sum;
    }

    public int partTwo(IntList numbers) {
        IntList slidingWindowSums = new IntList().init();

        int i = 0;
        while (i < numbers.size - 2) {
            int first = numbers.data[i];
            int second = numbers.data[i + 1];
            int third = numbers.data[i + 2];
            slidingWindowSums.add(first + second + third);
            i = i + 1;
        }

        return partOne(slidingWindowSums);
    }
}

class IntList {
    public int[] data;
    public int size;
    public int capacity;

    public IntList init() {
        this.capacity = 10;
        this.size = 0;
        this.data = new int[capacity];

        return this;
    }

    public void add(int number) {
        if (size >= capacity) {
            int[] newData = new int[2 * capacity];

            int i = 0;
            while (i < size) {
                newData[i] = data[i];
                i = i + 1;
            }

            data = newData;
            capacity = 2 * capacity;
        }
        data[size] = number;
        size = size + 1;
    }

    public int size() {
        return size;
    }
}

class InputHelper {
    public IntList readIntegers() throws Exception {
        int read;
        int currentNumber = 0;
        IntList numbers = new IntList().init();
        while ((read = System.in.read()) >= 0) {
            if (read == 10) {
                numbers.add(currentNumber);
                currentNumber = 0;
            } else {
                currentNumber = currentNumber * 10 + (read - 48);
            }
        }
        return numbers;
    }
}

class OutputHelper {
    public void printSolution(int solution) {
        System.out.write(83);
        System.out.write(111);
        System.out.write(108);
        System.out.write(117);
        System.out.write(116);
        System.out.write(105);
        System.out.write(111);
        System.out.write(110);
        System.out.write(58);
        System.out.write(32);
        System.out.write(27);
        System.out.write(91);
        System.out.write(51);
        System.out.write(52);
        System.out.write(59);
        System.out.write(49);
        System.out.write(109);
        System.out.println(solution);
        System.out.write(27);
        System.out.write(91);
        System.out.write(48);
        System.out.write(109);
        System.out.flush();
    }

    public void printDay(int day) {
        System.out.write(27);
        System.out.write(91);
        System.out.write(51);
        System.out.write(49);
        System.out.write(59);
        System.out.write(49);
        System.out.write(109);
        System.out.write(35);
        System.out.write(35);
        System.out.write(32);
        System.out.write(68);
        System.out.write(97);
        System.out.write(121);
        System.out.write(32);
        System.out.println(day);
        System.out.write(27);
        System.out.write(91);
        System.out.write(48);
        System.out.write(109);
        System.out.flush();
    }

}
