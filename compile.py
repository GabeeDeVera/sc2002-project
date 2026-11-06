import subprocess

import sys
import os

from time import sleep

main_dir = 'src\\main\\java'

for (dirname, dirs, files) in os.walk(main_dir):
   for file in files:
      if file.endswith('.class'):
          source_file = os.path.join(dirname, file)
          os.remove(source_file)

sleep(0.01)

subprocess.call("javac src\\main\\java\\Main.java")