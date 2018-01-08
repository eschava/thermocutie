from collections import OrderedDict
import os
import shutil


from .System import System

# TODO: add xml persisting


class Cutie:
    def __init__(self, folder):
        self.folder = folder
        self.systems = OrderedDict()

    def load(self):
        for name in os.listdir(self.folder):
            sub_folder = os.path.join(self.folder, name)
            if os.path.isdir(sub_folder):
                system = System(name, '?')
                system.load(sub_folder)
                self.systems[system.name] = system

    def get_systems(self):
        return self.systems.values()

    def get_system(self, name):
        return self.systems[name]

    def add_system(self, name, title):
        system = System(name, title)
        self.systems[name] = system
        sub_folder = os.path.join(self.folder, name)
        os.mkdir(sub_folder)
        system.save(sub_folder)

    def change_system(self, name, title):
        system = self.systems[name]
        system.title = title
        sub_folder = os.path.join(self.folder, name)
        system.save(sub_folder)

    def delete_system(self, name):
        del self.systems[name]
        sub_folder = os.path.join(self.folder, name)
        shutil.rmtree(sub_folder, ignore_errors=True)


