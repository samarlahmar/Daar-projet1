import os
import sys


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

    output = "RegEx;Book;BookSize;ParsingTime;TreeToNDFA;NDFAtoDFA;DFAtoMinDFA;MatchingTime;EgrepTime;KMPTime;\n"
    for reg in regLS:
        for book in Books:
            errorprint("Book: {0} Reg: {1}".format(book, reg))
            cmd = "java regEx/RegEx \"{0}\" {1}".format(reg, book)
            output += os.popen(cmd).readlines()[-1].replace('\n', '') + '\n'

    return output


with open(currentDir + '/data.csv', 'w') as f:
    f.write(runTests())
    f.close()
