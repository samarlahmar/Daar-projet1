import os
import sys
import pandas as pd
import matplotlib as plt


currentDir = os.path.abspath(os.path.dirname(__file__))


def errorprint(error):
    print(error, file=sys.stderr)


def readFile(filename):
    output = []
    with open(filename, 'r') as f:
        for line in f:
            line = line.replace('\n', '')
            output.append(line)
        f.close()
    return output


def runTests():
    regLS = readFile(currentDir + '/regexp.txt')
    Books = os.listdir(os.path.join(currentDir, 'Books'))
    Books = [os.path.join(currentDir, 'Books', book) for book in Books]

    pathToJava = os.path.join(currentDir, '../regEx')+'/'
    os.system("javac {0}*.java {0}Helpers/*.java  -cp {0}Libs/junit-4.10.jar".format(
        pathToJava))

    output = "RegEx;Book;BookSize;Parsing;TreeToNDFA;Determine;Minimize;Matching;Egrep;KMP\n"
    for reg in regLS:
        for book in Books:
            errorprint("Book: {0} Reg: {1}".format(book, reg))
            cmd = "java regEx/RegEx \"{0}\" {1}".format(reg, book)
            output += os.popen(cmd).readlines()[-1].replace('\n', '') + '\n'

    with open(currentDir + '/data.csv', 'w') as f:
        f.write(output)
        f.close()


def readData():
    data = pd.read_csv(currentDir + '/data.csv', sep=';')
    data = data.dropna()
    data = data.drop_duplicates()
    data = data.reset_index(drop=True)
    return data


runTests()
frame = readData()
""" Add a total time column without using the two last columns and the book size """
frame['TotalTime'] = frame.iloc[:, 3:-2].sum(axis=1)
""" Put the total time column before the egrep column """
cols = list(frame.columns.values)
cols.pop(cols.index('Egrep'))
frame = frame[cols+['Egrep']]
""" Bytes to KB """
frame['BookSize'] = frame['BookSize'].apply(lambda x: x/1000)


def coolBarPlot():
    """ Change color of each bar """
    plt.pyplot.gca().get_children()[0].set_color('r')
    plt.pyplot.gca().get_children()[1].set_color('g')
    plt.pyplot.gca().get_children()[2].set_color('b')
    plt.pyplot.gca().get_children()[3].set_color('y')
    plt.pyplot.gca().get_children()[4].set_color('c')
    plt.pyplot.gca().get_children()[5].set_color('m')
    """ Rotate the x axis labels """
    plt.pyplot.xticks(rotation=0)

    """ Write the mean value on each bar """
    for p in plt.pyplot.gca().patches:
        plt.pyplot.gca().annotate(str(round(p.get_height(), 2)),
                                  (p.get_x() * 1.005, p.get_height() * 1.005))


def timeMeans():
    means = frame.iloc[:, 4:]
    means = means.drop('KMP', axis=1)
    """ Get the mean of each column and keep columns names """
    means = means.mean()
    means = means.to_frame()
    means.plot(kind='bar')
    coolBarPlot()
    plt.pyplot.title('Mean time of each step vs Egrep time')
    plt.pyplot.ylabel('Time (ms)')
    plt.pyplot.legend('')
    plt.pyplot.savefig(currentDir + '/means.png')


def timeMeansWithKMP():
    means = frame.iloc[:, 8:]
    means = means.rename(columns={'TotalTime': 'Project'})
    means = means[means['KMP'] != -1]
    means = means.mean()
    means.sort_values(inplace=True)
    means = means.to_frame()
    means.plot(kind='bar')
    coolBarPlot()
    plt.pyplot.title('Project vs Egrep vs KMP mean time')
    plt.pyplot.ylabel('Time (ms)')
    plt.pyplot.legend('')
    plt.pyplot.savefig(currentDir + '/meansWithKMP.png')


def curvesWithKMP():
    f = frame.iloc[:, 8:]
    """ Put back the book size column """
    f = f.join(frame['BookSize'])
    """ Keep only rows where KMPTime is not -1 """
    f = f[f['KMP'] != -1]
    f = f.groupby(['BookSize']).mean()
    f.plot()
    f.rolling(window=5).mean().plot(linestyle='--')
    """ Set the title of the plot """
    plt.pyplot.title('Project vs Egrep vs KMP mean time')
    plt.pyplot.ylabel('Time (ms)')
    plt.pyplot.legend('')
    plt.pyplot.xlabel('Kilo Bytes')
    plt.pyplot.gca().get_lines()[0].set_label('KMP')
    plt.pyplot.gca().get_lines()[1].set_label('Project')
    plt.pyplot.gca().get_lines()[2].set_label('Egrep')
    plt.pyplot.legend()
    plt.pyplot.savefig(currentDir + '/curvesWithKMP.png')


def curve():
    f = frame.iloc[:, 8:]
    """ Put back the book size column """
    f = f.join(frame['BookSize'])
    f = f.drop('KMP', axis=1)
    f = f.groupby(['BookSize']).mean()
    f.plot()
    f.rolling(window=5).mean().plot(linestyle='--')
    """ Set the title of the plot """
    plt.pyplot.title('Project vs Egrep vs KMP mean time')
    plt.pyplot.ylabel('Time (ms)')
    plt.pyplot.legend('')
    plt.pyplot.xlabel('Kilo Bytes')
    plt.pyplot.gca().get_lines()[0].set_label('Project')
    plt.pyplot.gca().get_lines()[1].set_label('Egrep')
    plt.pyplot.legend()

    """ Save the plot """
    plt.pyplot.savefig(currentDir + '/curve.png')


timeMeans()
timeMeansWithKMP()
curvesWithKMP()
curve()
