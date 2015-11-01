class Foo(object):
	bar = 1
	def bah(self):
		Foo.bar = 2

f = Foo()
f.bah()
print Foo.bar


