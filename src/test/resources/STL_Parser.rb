#!/usr/local/bin ruby
#encoding: utf-8

# used to generate ascii versions of the stl files included here
# source: https://gist.github.com/firemiles/0d666fe7cbe6b619742c

class STL_Parser
    def initialize(out)
        @out = out
    end
    def filename(name, &block)
        @out << "solid #{name}\n"
        block.call
        @out << "endsolid #{name}\n"
    end
    def face(&block)
        @out << "facet normal "
        block.call
        @out << "endfacet\n"
    end
    def normal(vector)
        x, y, z = vector
        @out << "#{x} #{y} #{z}\n"
    end
    def outer(&block)
        @out << "  outer loop\n"
        block.call
        @out << "  endloop\n"
    end
    def vertex(vector)
        x, y, z = vector
        @out << " "*4 << "vertex #{x} #{y} #{z}\n"
    end
end

begin
if ARGV.size != 1
    puts "Usage: ./stl_parser.rb filename"
    exit
end
parser = STL_Parser.new(STDOUT)
binary_file = ARGV[0]
binary = File.open(binary_file, 'rb')

binary.seek(80, IO::SEEK_SET)
faces = binary.read(4).unpack('V')[0]

parser.filename "untitled" do
    faces.times do |i|
        parser.face do
            parser.normal binary.read(12).unpack('FFF')
            parser.outer do
                parser.vertex binary.read(12).unpack('FFF')
                parser.vertex binary.read(12).unpack('FFF')
                parser.vertex binary.read(12).unpack('FFF')
            end
            binary.seek(2, IO::SEEK_CUR)
        end
    end
end

rescue => e
    puts "error #{e}"
end
