class Point {
    public int x;
    public int y;

    public boolean cmp(Point other) {
        return y < other.y || (y == other.y && x <= other.x);
    }
}

class Line {
    public Point start;
    public Point end;

    public int id;

    public boolean cut(Line other) {
        return false;
    }
}

class LineSorter {

    public void sortPointsInLines(Line[] lines, int length) {
        int i = 0;
        while(i < length) {
            if (lines[i].start.y < lines[i].end.y || (lines[i].start.y == lines[i].end.y && lines[i].start.x > lines[i].end.x)) {
                Point tmp = lines[i].start;
                lines[i].start = lines[i].end;
                lines[i].end = tmp;
            }
            i = i + 1;
        }
    }

    public void sortPointsByStart(Line[] lines, int start, int end) {
        if (end - start == 1) {
            return;
        }
        int mid = (start + end) / 2;
        sortPointsByStart(lines, start, mid);
        sortPointsByStart(lines, mid, end);

        int currentLength = end - start;
        Line[] tmpArray = new Line[currentLength];
        int i = start;
        int s = start;
        int t = mid;
        boolean shouldBreak = false;
        while(!shouldBreak && i < currentLength) {
            if (lines[s].start.cmp(lines[t].start)) {
                tmpArray[i] = lines[s];
                s = s + 1;
            } else {
                tmpArray[i] = lines[t];
                t = t + 1;
            }
            i = i + 1;
            if (s == mid || t == end) {
                shouldBreak = true;
            }
        }
        while (s < mid) {
            tmpArray[i] = lines[s];
            i = i + 1;
            s = s + 1;
        }
        while (t < end) {
            tmpArray[i] = lines[t];
            i = i + 1;
            t = t + 1;
        }
        i = 0;
        while(i < currentLength) {
            lines[start + i] = tmpArray[i];
        }
    }

    public void sortPointsByEnd(Line[] lines, int start, int end) {
        if (end - start == 1) {
            return;
        }
        int mid = (start + end) / 2;
        sortPointsByStart(lines, start, mid);
        sortPointsByStart(lines, mid, end);

        int currentLength = end - start;
        Line[] tmpArray = new Line[currentLength];
        int i = start;
        int s = start;
        int t = mid;
        boolean shouldBreak = false;
        while(!shouldBreak && i < currentLength) {
            if (lines[s].end.cmp(lines[t].end)) {
                tmpArray[i] = lines[s];
                s = s + 1;
            } else {
                tmpArray[i] = lines[t];
                t = t + 1;
            }
            i = i + 1;
            if (s == mid || t == end) {
                shouldBreak = true;
            }
        }
        while (s < mid) {
            tmpArray[i] = lines[s];
            i = i + 1;
            s = s + 1;
        }
        while (t < end) {
            tmpArray[i] = lines[t];
            i = i + 1;
            t = t + 1;
        }
        i = 0;
        while(i < currentLength) {
            lines[start + i] = tmpArray[i];
        }
    }
}

class SweeplineEvent {
    int lineID;
    int crossLineID;
    int eventID;
    boolean start;
}

class SweeplineStatus {
    
}

class SweeplineAlgorithmus {
    Line[] lines;
    int num_lines;
    SweeplineEvent[] eventqueue;
    int eventlength;
    int size;
    int pos;

    public void init(Line[] lines, int num_lines) {
        eventqueue = new SweeplineEvent[8];
        size = 8;
        eventlength = 0;
        pos = 0;
        this.num_lines = num_lines;
        this.lines = lines;
        Line[] start = new Line[num_lines];
        Line[] end = new Line[num_lines];

        int i = 0;
        while(i < num_lines) {
            start[i] = lines[i];
            end[i] = lines[i]; 
        }
        
        LineSorter sorter = new LineSorter();
        sorter.sortPointsByStart(start, 0, num_lines);
        sorter.sortPointsByEnd(end, 0, num_lines);

        int s = 0;
        int e = 0;
        while(s < num_lines) {
            addEvent(1, start[s].id, start[s].id, true);
            s++;
        }
        while (e < num_lines) {
            addEvent(2, start[e].id, start[s].id, false);
            e++;
        }
    }

    public int size() {
        return eventlength - pos;
    }

    public void addEvent(int eventID, int lineID, int otherLineID, boolean start) {
        if (eventlength + 1 == size) {
            if (eventlength - pos > size * 3 / 4)
            size = size * 2;
            SweeplineEvent[] tmp = new SweeplineEvent[size];
            int i = 0;
            while(i < eventlength) {
                tmp[i] = eventqueue[pos + i];
            }
            eventqueue = tmp;
            eventlength = eventlength - pos;
            pos = 0;
        }
        int i = 0;
        SweeplineEvent se = new SweeplineEvent();
        se.eventID = eventID;
        se.lineID = lineID;
        se.crossLineID = otherLineID;
        se.start = start;
        while(i < eventlength) {
            Point cur;
            Point insert;
            if (eventqueue[i].start) {
                cur = lines[eventqueue[pos + i].lineID].start;
            } else {
                cur = lines[eventqueue[pos + i].lineID].end;
            }
            if (se.start) {
                insert = lines[se.lineID].start;
            } else {
                insert = lines[se.lineID].end;
            }

            if (!cur.cmp(insert)) {
                SweeplineEvent ev = eventqueue[pos + i];
                eventqueue[pos + i] = se;
                se = ev;
            }
            i = i + 1;
        }
        eventqueue[pos + eventlength] = se;
        eventlength = eventlength + 1;
    }

    public void handleEvent() {
        SweeplineEvent event = eventqueue[pos];
        pos++;
        if (size() < size / 4 && size > 8) {
            size = size / 2;
            SweeplineEvent[] tmp = new SweeplineEvent[size];
            int i = 0;
            while(i < eventlength) {
                tmp[i] = eventqueue[pos + i];
            }
            eventqueue = tmp;
            eventlength = eventlength - pos;
            pos = 0;
        }

        if (event.eventID == 1) { /*start*/
            
        } else if (event.eventID == 2) { /*end*/

        
        } else { /* event.eventID == 3 : crossing*/

        }
    }

    public void checkForCrossings() {
        while (size() > 0) {
            handleEvent();
        }
    }
}

class Generator {

    public int last;
    public int prime1;
    public int prime2;
    public int prime3;
    public int prime4;

    public void init() {
        last = 234;
        prime1 = 54643481;
        prime2 = 809357;
        prime3 = 12923359;
        prime4 = 783931;
    }

    public int randomInt() {
        last = last * prime1;
        last = last + prime2;
        last = last * prime3;
        last = last + prime4;
        return last;
    }

    public Point generatePoint() {
        Point p = new Point();
        p.x = randomInt() % 64;
        p.y = randomInt() % 64;
        return p;
    }

    public Line generate(int id) {
        Line line = new Line();
        line.id = id;
        line.start = generatePoint();
        line.end = generatePoint();
        if (line.start.y > line.end.y || (line.start.y > line.end.y && line.start.x > line.end.y)) {
            Point tmp = line.start;
            line.start = line.end;
            line.end = tmp;
        }
        return line;
    }

}

class Main {

    public static void main(String[] args) {
        int num_lines = 20;
        Line[] lines = new Line[num_lines];

        Generator gen = new Generator();
        gen.init();

        int i = 0;
        while(i < num_lines) {
            lines[i] = gen.generate(i);
            i = i + 1;
        }
        SweeplineAlgorithmus sa = new SweeplineAlgorithmus();
        sa.init(lines, num_lines);
        sa.checkForCrossings();
    }
}

