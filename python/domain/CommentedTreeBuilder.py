from xml.etree import ElementTree


# from https://stackoverflow.com/questions/33573807/faithfully-preserve-comments-in-parsed-xml-python-2-7
class CommentedTreeBuilder(ElementTree.TreeBuilder):
    def __init__(self, *args, **kwargs):
        super(CommentedTreeBuilder, self).__init__(*args, **kwargs)

    def comment(self, data):
        self.start(ElementTree.Comment, {})
        self.data(data)
        self.end(ElementTree.Comment)

    @classmethod
    def parse(cls, f):
        return ElementTree.parse(f, ElementTree.XMLParser(target=CommentedTreeBuilder()))