################################################################################
#  Makefile
# Created on: Feb 25, 2019
#     Author: kychu
################################################################################

Project := kyserial

RM    := @rm -rf
MKDIR := @mkdir

TopPath := $(shell pwd)
BuildPath := $(TopPath)/build

INCS := -I$(TopPath)/inc \
        -I${JAVA_HOME}/include \
        -I${JAVA_HOME}/include/linux

LIBS := 
#-lpthread

OBJS :=

# All of the sources participating in the build are defined here
-include src/subdir.mk

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: $(Project) | $(BuildPath)

$(BuildPath):
	$(MKDIR) -p $@

$(OBJ_DIRS):
	$(MKDIR) -p $@

# Tool invocations
$(Project): $(OBJS)
	@echo ''
	@echo ' Building target lib$@.so'
	@gcc -shared $(OBJS) $(LIBS) -o lib$@.so
	@echo ' Completed.'

# Other Targets
clean:
	-$(RM) $(BuildPath) lib$(Project).so
	-@echo 'Cleaned.'

.PHONY: all clean

