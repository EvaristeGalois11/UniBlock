revision := 2

thesis.pdf: thesis.tex sezioni/titlePage.tex sezioni/chapter1.tex sezioni/chapter2.tex sezioni/chapter3.tex sezioni/bibliografia.bib
	@mkdir -p .bin/sezioni
	latexmk -pdf -output-directory=.bin thesis.tex
	@mv .bin/thesis.pdf ./thesis-revision-$(revision).pdf

clean:
	rm -rf .bin
