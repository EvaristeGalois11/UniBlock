thesis.pdf: thesis.tex sezioni/titlePage.tex sezioni/bibliografia.tex sezioni/esercizi.tex
	@mkdir -p .bin/sezioni
	latexmk -pdf -output-directory=.bin thesis.tex
	@mv .bin/thesis.pdf ./

clean:
	rm -rf .bin
