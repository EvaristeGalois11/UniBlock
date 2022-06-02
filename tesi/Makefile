thesis.pdf: thesis.tex sezioni/titlePage.tex sezioni/chapter1.tex sezioni/chapter2.tex sezioni/bibliografia.bib sezioni/esercizi.tex
	@mkdir -p .bin/sezioni
	latexmk -pdf -output-directory=.bin thesis.tex
	@mv .bin/thesis.pdf ./

clean:
	rm -rf .bin
